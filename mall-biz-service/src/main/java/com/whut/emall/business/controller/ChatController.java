package com.whut.emall.business.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.business.entity.enums.MessageType;
import com.whut.emall.business.service.ChatService;
import com.whut.emall.business.vo.ChatMessageListVO;
import com.whut.emall.business.vo.ChatSessionVO;
import com.whut.emall.common.entity.ApiResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping("/chat")
@Tag(name = "客服咨询接口", description = "用于会员和客户之间咨询聊天的模块")
public class ChatController {
    @Resource ChatService chatService;

    @Operation(summary = "发起客服咨询", description = "用户发起客服咨询，创建会话，自动分配客服或进入AI模式")
    @ApiResponse(responseCode = "200", description = "会话创建成功")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("start")
    public ApiResult<ChatSessionVO> startChat(
        @Parameter(hidden = true) @RequestHeader("X-User-Id") Integer uid,
        @RequestBody @Valid ChatStartDTO dto
    ) {
        var vo = chatService.startChat(uid, dto.getSource(), dto.getSourceId(), dto.getFirstMessage());
        return ApiResult.ok("会话创建成功", vo);
    }

    @Operation(summary = "用户发送消息", description = "用户在会话中发送消息，根据模式决定走真人客服还是 AI 自动回复")
    @ApiResponse(responseCode = "200", description = "发送成功")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("message")
    public ApiResult<Void> sendMessage(
        @Parameter(hidden = true) @RequestHeader("X-User-Id") Integer uid,
        @RequestBody @Valid ChatMessageDTO dto
    ) {
        chatService.sendMessage(uid, dto.getSessionId(), dto.getContent(), dto.getMsgType(), dto.getExtraData());
        return ApiResult.ok("发送成功");
    }

    @Operation(summary = "获取消息列表", description = "获取当前会话的所有消息")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("messages")
    public ApiResult<ChatMessageListVO> listMessage(
        @Parameter(hidden = true) @RequestHeader("X-User-Id") Integer uid,
        Integer sessionId,
        @RequestParam(required = false, defaultValue = "1") Integer pageNum,
        @RequestParam(required = false, defaultValue = "20") Integer pageSize
    ) {
        return ApiResult.ok("获取成功", chatService.listMessages(uid, pageNum, pageSize, sessionId));
    }

    @Data
    static class ChatStartDTO {
        @NotBlank(message = "source不可为空") String source;
        String sourceId;
        @NotBlank(message = "firstMessage不可为空") String firstMessage;
    }
    @Data
    static class ChatMessageDTO {
        @NotNull(message = "sessionId不可为空") Integer sessionId;
        @NotBlank(message = "content不可为空") String content;
        @NotNull(message = "msgType不可为空") MessageType msgType;
        Map<String,Object> extraData;
    }
}
