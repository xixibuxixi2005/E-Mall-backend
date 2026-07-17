package com.whut.emall.business.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.business.entity.enums.CSStatusStatus;
import com.whut.emall.business.entity.enums.MessageType;
import com.whut.emall.business.entity.enums.SenderType;
import com.whut.emall.business.entity.enums.SessionStatus;
import com.whut.emall.business.service.ChatService;
import com.whut.emall.business.vo.AISuggestVO;
import com.whut.emall.business.vo.CSStatusVO;
import com.whut.emall.business.vo.ChatMessageListVO;
import com.whut.emall.business.vo.ChatMessageVO;
import com.whut.emall.business.vo.ChatSessionListVO;
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

@RestController
@RequestMapping("/chat/cs")
@Tag(name = "客服咨询接口（客服端）", description = "用于会员和客户之间咨询聊天的模块")
public class ChatCSController {
    @Resource ChatService chatService;

    @Operation(summary = "切换在线状态（上下班）", description = "客服切换在线/离线/忙碌状态")
    @ApiResponse(responseCode = "200", description = "状态切换成功")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("status")
    public ApiResult<CSStatusVO> changeStatus(
        @Parameter(hidden = true) @RequestHeader("X-User-Id") Integer uid,
        @RequestBody @Valid CSStatusDTO dto
    ) {
        return ApiResult.ok("状态切换成功", chatService.csSetStatus(uid, dto.getStatus()));
    }

    @Operation(summary = "获取待服务会话列表", description = "获取所有待服务/进行中的会话列表（排队中 + 服务中）")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("sessions")
    public ApiResult<ChatSessionListVO> listSessions(
        @RequestParam(required = false) SessionStatus status,
        @RequestParam(required = false, defaultValue = "1") Integer pageNum,
        @RequestParam(required = false, defaultValue = "20") Integer pageSize
    ) {
        return ApiResult.ok("获取成功", chatService.csListSessions(status, pageNum, pageSize));
    }

    @Operation(summary = "接管会话", description = "客服接管排队中的会话")
    @ApiResponse(responseCode = "200", description = "接管成功")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("takeover")
    public ApiResult<CSStatusVO> takeoverSession(
        @Parameter(hidden = true) @RequestHeader("X-User-Id") Integer uid,
        @RequestBody @Valid CSTakeoverDTO dto
    ) {
        return ApiResult.ok("接管成功", chatService.csTakeoverSession(uid, dto.getSessionId()));
    }

    @Operation(summary = "客服发送消息", description = "客服在会话中发送消息")
    @ApiResponse(responseCode = "200", description = "发送成功")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("message")
    public ApiResult<ChatMessageVO> sendMessage(
        @Parameter(hidden = true) @RequestHeader("X-User-Id") Integer uid,
        @RequestBody @Valid CSMessageDTO dto
    ) {
        return ApiResult.ok("发送成功", chatService.sendMessage(uid, SenderType.CS, dto.getSessionId(), dto.getContent(), dto.getMsgType(), dto.getExtraData()));
    }

    @Operation(summary = "AI 辅助回复建议（未实现）", description = "客服输入问题时，AI 生成回复建议（非流式，快速返回）")
    @ApiResponse(responseCode = "200", description = "生成成功")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("ai-suggest")
    public ApiResult<AISuggestVO> aiSuggest(
        @Parameter(hidden = true) @RequestHeader("X-User-Id") Integer uid,
        @RequestBody @Valid CSAISuggestDTO dto
    ) {
        return ApiResult.ok("生成成功", chatService.csAiSuggest(uid, dto.getSessionId(), dto.getUserQuestion()));
    }

    @Operation(summary = "切换 AI 托管模式（未实现）", description = "客服将会话切换至 AI 自动回复模式（用于下班后或繁忙时）")
    @ApiResponse(responseCode = "200", description = "生成成功")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("ai-mode")
    public ApiResult<ChatSessionVO> setAiMode(
        @RequestBody @Valid CSAIModeDTO dto
    ) {
        return ApiResult.ok("生成成功", chatService.csAiMode(dto.getSessionId(), dto.getAiMode()));
    }

    @Operation(summary = "结束会话（客服结束）", description = "客服结束当前会话")
    @ApiResponse(responseCode = "200", description = "会话已结束")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("end")
    public ApiResult<Void> endSession(
        @Parameter(hidden = true) @RequestHeader("X-User-Id") Integer uid,
        @RequestBody @Valid CSAIEndDTO dto
    ) {
        chatService.csEndSession(uid, dto.getSessionId(), dto.getSummary());
        return ApiResult.ok("会话已结束");
    }

    @Operation(summary = "获取消息列表", description = "获取指定会话的所有消息（客服端）")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("messages")
    public ApiResult<ChatMessageListVO> listMessage(
        @Valid @NotNull(message = "sessionId 不可为空")Integer sessionId,
        @RequestParam(required = false, defaultValue = "1") Integer pageNum,
        @RequestParam(required = false, defaultValue = "20") Integer pageSize
    ) {
        return ApiResult.ok("获取成功", chatService.listMessages(null, pageNum, pageSize, sessionId));
    }

    @Data
    static class CSStatusDTO {
        @NotNull(message = "status 不可为空") CSStatusStatus status;    
    }
    @Data
    static class CSTakeoverDTO {
        @NotNull(message = "sessionId 不可为空") Integer sessionId;    
    }
    @Data
    static class CSMessageDTO {
        @NotNull(message = "sessionId 不可为空") Integer sessionId;
        @NotBlank(message = "content 不可为空") String content;
        @NotNull(message = "msgType 不可为空") MessageType msgType;
        Map<String,Object> extraData;
    }
    @Data
    static class CSAISuggestDTO {
        @NotNull(message = "sessionId 不可为空") Integer sessionId;
        @NotBlank(message = "userQuestion 不可为空") String userQuestion;
    }
    @Data
    static class CSAIModeDTO {
        @NotNull(message = "sessionId 不可为空") Integer sessionId;
        @NotNull(message = "aiMode 不可为空") Boolean aiMode;
    }
    @Data
    static class CSAIEndDTO {
        @NotNull(message = "sessionId 不可为空") Integer sessionId;
        String summary;
    }
}
