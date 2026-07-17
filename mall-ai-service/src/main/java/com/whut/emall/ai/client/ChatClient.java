package com.whut.emall.ai.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "mall-biz-service", path = "/api")
public interface ChatClient {
    @GetMapping("/chat/cs/messages")
    Object getHistories(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam Integer sessionId);
}
