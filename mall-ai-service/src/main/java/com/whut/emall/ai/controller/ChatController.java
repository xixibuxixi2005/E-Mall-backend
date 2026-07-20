package com.whut.emall.ai.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.ai.client.BizClient;
import com.whut.emall.ai.service.ChatService;
import com.whut.emall.common.entity.ApiResult;
import com.whut.emall.common.vo.ChatMessageListVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
@Tag(name = "智能问答", description = "用于会员和客服之间咨询聊天的模块")
public class ChatController {
    @Resource ChatService chatService;
    @Resource BizClient bizClient;

    @Operation(summary = "智能问答（SSE流式）", description = "会员或客服向AI提问，AI返回流式回答")
    @ApiResponse(responseCode = "200", description = "流式返回中")
    @SecurityRequirement(name = "Authorization")
    @GetMapping(value = "stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(
        @RequestParam String question,
        @RequestParam(required = false) Integer sessionId,
        @RequestParam(required = false, defaultValue = "5") Integer topK
    ) {
        return chatService.streamChat(question, sessionId, topK);
    }

    @Operation(summary = "获取消息列表", description = "获取指定会话的所有消息")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("history")
    public ApiResult<ChatMessageListVO> listMessage(
        @Valid @NotNull(message = "sessionId 不可为空")Integer sessionId,
        @RequestParam(required = false, defaultValue = "1") Integer pageNum,
        @RequestParam(required = false, defaultValue = "20") Integer pageSize
    ) {
        return bizClient.getHistories(pageNum, pageSize, sessionId);
    }
}