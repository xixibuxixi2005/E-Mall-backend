package com.whut.emall.business.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.business.entity.ChatMessage;
import com.whut.emall.business.entity.ChatSession;
import com.whut.emall.business.entity.enums.MessageType;
import com.whut.emall.business.entity.enums.SenderType;
import com.whut.emall.business.mapper.ChatMessageMapper;
import com.whut.emall.business.mapper.ChatSessionMapper;
import com.whut.emall.business.vo.ChatMessageListVO;
import com.whut.emall.business.vo.ChatMessageVO;
import com.whut.emall.business.vo.ChatSessionVO;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.utils.UniqueRamdom;

import jakarta.annotation.Resource;

@Service
public class ChatService {
    @Resource ChatSessionMapper sessionMapper;
    @Resource ChatMessageMapper messageMapper;
    @Resource UniqueRamdom uniqueRamdom;

    @Transactional(rollbackFor = Exception.class)
    public ChatSessionVO startChat(Integer userId, String source, String sourceId, String firstMessage) {
        ChatSession session = new ChatSession();
        session.setSessionNo(uniqueRamdom.getUniqueDateLabel(4, "CS"));
        session.setUserId(userId);
        session.setSource(source);
        session.setSourceId(sourceId);
        session.setFirstMessage(firstMessage);

        // 分配客服

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

    public void sendMessage(Integer userId, Integer sessionId, String content, MessageType msgType, Map<String,Object> extraData) {
        ChatSession session = sessionMapper.selectById(sessionId);
        if (session == null || session.getUserId()!=userId)
            throw ApiException.err(404, "会话不存在");

        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setSenderType(SenderType.USER);
        message.setSenderId(userId);
        message.setContent(content);
        message.setMsgType(msgType);
        message.setExtraData(extraData);
        
        messageMapper.insert(message);
        sessionMapper.updateById(session);
    }

    public ChatMessageListVO listMessages(Integer userId, Integer pageNum, Integer pageSize, Integer sessionId) {
        ChatSession session = sessionMapper.selectById(sessionId);
        if (session == null || session.getUserId()!=userId)
            throw ApiException.err(404, "会话不存在");
        
        Page<ChatMessageVO> page = new Page<>(pageNum, pageSize);
        page = messageMapper.getVOsBySessionId(page, sessionId);
        return new ChatMessageListVO(page);
    }
}
