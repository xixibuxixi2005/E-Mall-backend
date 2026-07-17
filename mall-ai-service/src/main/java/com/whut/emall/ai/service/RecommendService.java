package com.whut.emall.ai.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whut.emall.ai.client.BizClient;
import com.whut.emall.ai.tools.AITools;
import com.whut.emall.ai.vo.RecommendListVO;
import com.whut.emall.ai.vo.RecommendsVO;
import com.whut.emall.common.entity.ApiException;

import jakarta.annotation.Resource;

@Service
public class RecommendService {
    @Resource BizClient bizClient;
    @Resource LLMService llmService;
    @Resource AITools aiTools;
    final ObjectMapper objectMapper = new ObjectMapper();

    public RecommendListVO recommendByBehavior(Integer userId, Integer size) throws Exception {
        String prompt = """
你是电商推荐引擎。请基于用户购物车和历史订单生成个性化商品推荐。
要求输出JSON，字段为recommendations数组。
score范围0到1，按相关性降序，最多%d个。
用户ID：%d
请优先推荐与购物车商品同类、互补、复购相关的商品。
""".formatted(size, userId);
        RecommendResponse response = llmService.customPromptStructCall(prompt, "请输出商品推荐结果", RecommendResponse.class, aiTools);
        List<RecommendsVO> recs = response == null || response.recommendations() == null ? List.of() : response.recommendations().stream()
            .sorted(Comparator.comparing(RecommendsVO::score, Comparator.nullsLast(Comparator.reverseOrder())))
            .limit(size)
            .toList();
        RecommendListVO vo = new RecommendListVO();
        vo.setRecommendations(recs);
        return vo;
    }

    public void refresh(Integer userId, String eventType, Integer productId) {
        if (userId == null || eventType == null) throw ApiException.err(400, "参数不能为空");
        // TODO: 实现刷新推荐
    }

    public record RecommendResponse(List<RecommendsVO> recommendations) {}
}
