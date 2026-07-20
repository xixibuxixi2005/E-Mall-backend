package com.whut.emall.business.service;

import java.sql.Timestamp;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.business.entity.CSStatus;
import com.whut.emall.business.entity.ChatMessage;
import com.whut.emall.business.entity.ChatSession;
import com.whut.emall.business.entity.enums.CSStatusStatus;
import com.whut.emall.business.entity.enums.SessionMode;
import com.whut.emall.business.entity.enums.SessionStatus;
import com.whut.emall.business.mapper.CSStatusMapper;
import com.whut.emall.business.mapper.ChatMessageMapper;
import com.whut.emall.business.mapper.ChatSessionMapper;
import com.whut.emall.business.vo.CSStatusVO;
import com.whut.emall.business.vo.ChatSessionListVO;
import com.whut.emall.business.vo.ChatSessionVO;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.entity.enums.MessageType;
import com.whut.emall.common.entity.enums.SenderType;
import com.whut.emall.common.utils.UniqueRamdom;
import com.whut.emall.common.vo.ChatMessageListVO;
import com.whut.emall.common.vo.ChatMessageVO;

import jakarta.annotation.Resource;

@Service
public class ChatService {
    @Resource ChatSessionMapper sessionMapper;
    @Resource ChatMessageMapper messageMapper;
    @Resource CSStatusMapper statusMapper;
    @Resource UniqueRamdom uniqueRamdom;

    @Transactional(rollbackFor = Exception.class)
    public ChatSessionVO startChat(Integer userId, String source, String sourceId, String firstMessage) {
        ChatSession session = new ChatSession();
        session.setSessionNo(uniqueRamdom.getUniqueDateLabel(4, "CS"));
        session.setUserId(userId);
        session.setSource(source);
        session.setSourceId(sourceId);
        session.setFirstMessage(firstMessage);

        // TODO: 分配客服

        sessionMapper.insert(session);

        ChatMessage message = new ChatMessage();
        message.setSessionId(session.getId());
        message.setSenderType(SenderType.USER);
        message.setSenderId(userId);
        message.setContent(firstMessage);
        message.setMsgType(MessageType.TEXT);

        messageMapper.insert(message);

        return sessionMapper.getVOById(session.getId());
    }

    public ChatMessageVO sendMessage(Integer userId, SenderType senderType, Integer sessionId, String content, MessageType msgType, Map<String,Object> extraData) {
        ChatSession session = sessionMapper.selectById(sessionId);
        if (session == null)
            throw ApiException.err(404, "会话不存在");
        if (senderType == SenderType.USER && session.getUserId()!=userId)
            throw ApiException.err(404, "会话不存在");
        if (session.getStatus() == SessionStatus.FINISHED)
            throw ApiException.err(403, "会话已结束");
        if (senderType == SenderType.CS && session.getCsId()!=userId)
            throw ApiException.err(403, "无法回复未接管的会话");

        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setSenderType(senderType);
        message.setSenderId(userId);
        message.setContent(content);
        message.setMsgType(msgType);
        message.setExtraData(extraData);
        
        messageMapper.insert(message);
        sessionMapper.updateById(session);

        if (senderType == SenderType.CS)
            messageMapper.readAllBySessionId(sessionId);

        return messageMapper.getVOById(message.getId());
    }

    public ChatMessageListVO listMessages(Integer userId, Integer pageNum, Integer pageSize, Integer sessionId) {
        ChatSession session = sessionMapper.selectById(sessionId);
        if (session == null || (userId!=null && session.getUserId()!=userId))
            throw ApiException.err(404, "会话不存在");
        
        Page<ChatMessageVO> page = new Page<>(pageNum, pageSize);
        page = messageMapper.getVOsBySessionId(page, sessionId);
        return new ChatMessageListVO(page);
    }

    public ChatSessionListVO listSessions(Integer userId, Integer pageNum, Integer pageSize) {
        Page<ChatSessionVO> page = new Page<>(pageNum, pageSize);
        return new ChatSessionListVO(sessionMapper.getVOs(page, userId, null));
    }

    public ChatSessionVO getSessionStatus(Integer userId, Integer sessionId) {
        ChatSessionVO vo = sessionMapper.getVOById(sessionId);
        if (vo == null || vo.getUserId()!=userId)
            throw ApiException.err(404, "会话不存在");
        
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public void endSession(Integer userId, Integer sessionId) {
        ChatSession session = sessionMapper.selectById(sessionId);
        if (session == null || session.getUserId()!=userId)
            throw ApiException.err(404, "会话不存在");
        if (session.getStatus()==SessionStatus.FINISHED)
            return;
        
        CSStatus csStatus = getCSStatusByCsId(session.getCsId());
        if (csStatus != null) {
            csStatus.setCurrentCount(csStatus.getCurrentCount() - 1);
            statusMapper.updateById(csStatus);
        } 

        session.setStatus(SessionStatus.FINISHED);
        session.setEndTime(new Timestamp(System.currentTimeMillis()));
        sessionMapper.updateById(session);
    }

    private CSStatus getCSStatusByCsId(Integer csId) {
        LambdaQueryWrapper<CSStatus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CSStatus::getCsId, csId);
        return statusMapper.selectOne(wrapper);
    }

// ============================ 客服端 ============================

    public CSStatusVO csGetStatus(Integer userId) {
        CSStatus csStatus = getCSStatusByCsId(userId);
        if (csStatus == null)
            throw ApiException.err(404, "客服不存在");
        return statusMapper.getVOByCsId(userId);
    }

    public CSStatusVO csSetStatus(Integer userId, CSStatusStatus status) {
        CSStatus csStatus = getCSStatusByCsId(userId);
        if (csStatus == null)
            throw ApiException.err(404, "客服不存在");
        csStatus.setStatus(status);

        statusMapper.updateById(csStatus);
        return statusMapper.getVOByCsId(userId);
    }

    public ChatSessionListVO csListSessions(SessionStatus status, Integer pageNum, Integer pageSize) {
        Page<ChatSessionVO> page = new Page<>(pageNum, pageSize);
        return new ChatSessionListVO(sessionMapper.getVOs(page, null, status));
    }

    @Transactional(rollbackFor = Exception.class)
    public CSStatusVO csTakeoverSession(Integer csId, Integer sessionId) {
        ChatSession session = sessionMapper.selectById(sessionId);
        if (session == null)
            throw ApiException.err(404, "会话不存在");
        if (session.getStatus() != SessionStatus.WAITING)
            throw ApiException.err(403, "会话不在排队中，无法接管");

        CSStatus csStatus = getCSStatusByCsId(csId);
        if (csStatus == null || csStatus.getStatus() == CSStatusStatus.OFFLINE)
            throw ApiException.err(403, "客服不在线，无法接管会话");
        if (csStatus.getStatus() == CSStatusStatus.BUSY)
            throw ApiException.err(403, "客服正在服务其他会话，无法接管会话");

        session.setCsId(csId);
        session.setStatus(SessionStatus.SERVING);
        session.setMode(SessionMode.CS);
        sessionMapper.updateById(session);

        csStatus.setCurrentCount(csStatus.getCurrentCount() + 1);
        statusMapper.updateById(csStatus);

        return statusMapper.getVOByCsId(csId);
    }

    @Transactional(rollbackFor = Exception.class)
    public ChatSessionVO csAiMode(Integer sessionId, Boolean aiMode) {
        ChatSession session = sessionMapper.selectById(sessionId);
        if (session == null)
            throw ApiException.err(404, "会话不存在");
        if (aiMode) {
            session.setMode(SessionMode.AI);
            Integer csId = session.getCsId();
            if (csId != null) {
                CSStatus csStatus = getCSStatusByCsId(csId);
                csStatus.setCurrentCount(csStatus.getCurrentCount() - 1);
                statusMapper.updateById(csStatus);
            }
        } else {
            session.setMode(SessionMode.CS);
            Integer csId = session.getCsId();
            if (csId != null) {
                CSStatus csStatus = getCSStatusByCsId(csId);
                if (csStatus.getStatus() == CSStatusStatus.OFFLINE)
                    throw ApiException.err(403, "客服不在线，无法切换至客服模式");
                if (csStatus.getStatus() == CSStatusStatus.BUSY)
                    throw ApiException.err(403, "客服正在服务其他会话，无法切换至客服模式");
                csStatus.setCurrentCount(csStatus.getCurrentCount() + 1);
                statusMapper.updateById(csStatus);
            }
        }
        sessionMapper.updateById(session);
        // TODO: 使用RAG实现对话接口
        return sessionMapper.getVOById(sessionId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void csEndSession(Integer csId, Integer sessionId, String summary) {
        ChatSession session = sessionMapper.selectById(sessionId);
        if (session == null)
            throw ApiException.err(404, "会话不存在");
        if (session.getCsId() == null || !session.getCsId().equals(csId))
            throw ApiException.err(403, "无法结束未接管的会话");
        if (session.getStatus() == SessionStatus.FINISHED)
            return;

        if (session.getMode() == SessionMode.CS) {
            CSStatus csStatus = getCSStatusByCsId(csId);
            csStatus.setCurrentCount(csStatus.getCurrentCount() - 1);
            statusMapper.updateById(csStatus);
        }

        session.setStatus(SessionStatus.FINISHED);
        session.setEndTime(new Timestamp(System.currentTimeMillis()));
        // session.setSummary(summary); // TODO: 结束会话时，客服可以填写总结
        sessionMapper.updateById(session);
    }
}
