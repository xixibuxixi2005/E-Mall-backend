package com.whut.emall.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.business.entity.ChatMessage;
import com.whut.emall.business.entity.ChatSession;
import com.whut.emall.business.entity.enums.SessionStatus;
import com.whut.emall.business.mapper.ChatMessageMapper;
import com.whut.emall.business.mapper.ChatSessionMapper;
import com.whut.emall.business.service.ChatService;
import com.whut.emall.business.vo.ChatSessionListVO;
import com.whut.emall.common.entity.enums.MessageType;
import com.whut.emall.common.entity.enums.SenderType;
import com.whut.emall.common.vo.ChatMessageListVO;

import jakarta.annotation.Resource;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = BusinessApplication.class)
public class ChatServiceTest {

    @Resource
    private ChatSessionMapper sessionMapper;

    @Resource
    private ChatMessageMapper messageMapper;

    @Resource
    private ChatService chatService;

    private static Integer testSessionId;
    private static Integer testMessageId;
    private static final Integer userId = 7;

    @Test
    @Order(1)
    public void testSelectSessionPage() {
        Page<ChatSession> page = new Page<>(1, 10);
        Page<ChatSession> result = sessionMapper.selectPage(page, null);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getRecords());
        System.out.println("会话总数：" + result.getTotal());
    }

    @Test
    @Order(2)
    public void testStartChat() {
        var vo = chatService.startChat(userId, "PRODUCT", "1", "测试消息：这是一个测试");
        Assertions.assertNotNull(vo);
        Assertions.assertNotNull(vo.getSessionNo());
        testSessionId = vo.getId();
        System.out.println("创建会话ID：" + testSessionId);
    }

    @Test
    @Order(3)
    public void testGetSessionStatus() {
        if (testSessionId == null) return;
        var vo = chatService.getSessionStatus(userId, testSessionId);
        Assertions.assertNotNull(vo);
        Assertions.assertEquals(SessionStatus.WAITING, vo.getStatus());
    }

    @Test
    @Order(4)
    public void testSendMessage() {
        if (testSessionId == null) return;
        var vo = chatService.sendMessage(userId, SenderType.USER, testSessionId, "测试消息内容", MessageType.TEXT, null);
        Assertions.assertNotNull(vo);
        Assertions.assertNotNull(vo.getContent());
        testMessageId = vo.getId();
    }

    @Test
    @Order(5)
    public void testListMessages() {
        if (testSessionId == null) return;
        ChatMessageListVO vo = chatService.listMessages(userId, 1, 10, testSessionId);
        Assertions.assertNotNull(vo);
        Assertions.assertNotNull(vo.getList());
        System.out.println("消息总数：" + vo.getTotal());
    }

    @Test
    @Order(6)
    public void testListSessions() {
        ChatSessionListVO vo = chatService.listSessions(userId, 1, 10);
        Assertions.assertNotNull(vo);
        Assertions.assertNotNull(vo.getList());
    }

    @Test
    @Order(7)
    public void testSelectMessagePage() {
        Page<ChatMessage> page = new Page<>(1, 10);
        Page<ChatMessage> result = messageMapper.selectPage(page, null);
        Assertions.assertNotNull(result);
        System.out.println("消息总数：" + result.getTotal());
    }

    @Test
    @Order(8)
    public void testGetVOsBySessionId() {
        if (testSessionId == null) return;
        var page = messageMapper.getVOsBySessionId(new Page<>(1, 10), testSessionId);
        Assertions.assertNotNull(page);
    }

    @Test
    @Order(9)
    public void testReadAllBySessionId() {
        if (testSessionId == null) return;
        int rows = messageMapper.readAllBySessionId(testSessionId);
        Assertions.assertTrue(rows >= 0);
    }

    @Test
    @Order(10)
    public void testCsListSessions() {
        var vo = chatService.csListSessions(null, 1, 10);
        Assertions.assertNotNull(vo);
        Assertions.assertNotNull(vo.getList());
    }

    @Test
    @Order(11)
    public void testEndSession() {
        if (testSessionId == null) return;
        ChatSession session = sessionMapper.selectById(testSessionId);
        if (session != null && session.getCsId() == null) {
            session.setCsId(2);
            sessionMapper.updateById(session);
        }
        chatService.endSession(userId, testSessionId);
        ChatSession ended = sessionMapper.selectById(testSessionId);
        Assertions.assertEquals(SessionStatus.FINISHED, ended.getStatus());
        System.out.println("结束会话ID：" + testSessionId);
    }
}
