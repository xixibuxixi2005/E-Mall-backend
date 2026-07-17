package com.whut.emall.ai.controller;

import java.util.Map;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whut.emall.ai.client.ChatClient;
import com.whut.emall.ai.service.ChatService;
import com.whut.emall.common.entity.ApiResult;

import feign.FeignException;
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
@Tag(name = "智能问答（未实现）", description = "用于会员和客服之间咨询聊天的模块")
public class ChatController {
    @Resource ChatService chatService;
    @Resource ChatClient chatClient;

    @Operation(summary = "智能问答（SSE流式）", description = "会员或客服向AI提问，AI返回流式回答")
    @ApiResponse(responseCode = "200", description = "流式返回中")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("stream")
    public Flux<ServerSentEvent<String>> streamChat(
        @RequestParam String question,
        @RequestParam(required = false) Integer sessionId,
        @RequestParam(required = false, defaultValue = "5") Integer topK
    ) {
        return chatService.streamChat(question, sessionId, topK)
            .map(msg -> ServerSentEvent.builder(msg).event("message").build())
            .concatWith(Flux.just(ServerSentEvent.builder("[DONE]").event("message").build()));
    }

    @Operation(summary = "获取消息列表", description = "获取指定会话的所有消息")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("history")
    public Object listMessage(
        @Valid @NotNull(message = "sessionId 不可为空")Integer sessionId,
        @RequestParam(required = false, defaultValue = "1") Integer pageNum,
        @RequestParam(required = false, defaultValue = "20") Integer pageSize
    ) throws Exception {
       try{
          return chatClient.getHistories(pageNum, pageSize, sessionId);
       } catch (FeignException err) {
            Map<String,Object> obj = new ObjectMapper().readValue(err.contentUTF8(), Map.class);
            return new ApiResult<>((int)obj.get("code"), (String)obj.get("msg"), (Object)obj.get("data"));
       }
    }
}

/**
 * 1.2 智能问答（SSE流式）
GET /api/ai/chat/stream
权限：ADMIN / CS（也可对MEMBER开放，根据业务需要）
请求参数（Query）：
参数	类型	必填	说明
question	string	是	用户提问（需URL Encode）
sessionId	string	否	会话ID（用于关联历史）
topK	int	否	检索文档块数量，默认5
响应：text/event-stream

事件格式：
event: message，data: 单字/词组
最终以 data: [DONE] 结束
引用来源通过最后一条 event: source 返回（JSON格式）
示例（前端监听）：
const es = new EventSource('/api/ai/chat/stream?question=支持快充吗&sessionId=10001');
es.addEventListener('message', (e) => {
  if (e.data === '[DONE]') { es.close(); return; }
  appendText(e.data);
});
es.addEventListener('source', (e) => {
  const sources = JSON.parse(e.data);
  showReferences(sources);
});
 */
