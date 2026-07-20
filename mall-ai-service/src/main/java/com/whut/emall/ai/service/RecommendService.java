package com.whut.emall.ai.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whut.emall.ai.client.BizClient;
import com.whut.emall.ai.entity.UserRecommend;
import com.whut.emall.ai.mapper.UserRecommendMapper;
import com.whut.emall.ai.tools.AITools;
import com.whut.emall.ai.vo.RecommendListVO;
import com.whut.emall.ai.vo.RecommendVO;
// import com.whut.emall.common.entity.ApiException;

import jakarta.annotation.Resource;

@Service
public class RecommendService {
    @Resource BizClient bizClient;
    @Resource LLMService llmService;
    @Resource AITools aiTools;
    @Resource UserRecommendMapper userRecommendMapper;
    final ObjectMapper objectMapper = new ObjectMapper();

    private void updateUserRecommend(int userId, int size) {
        String prompt = """
你是电商推荐引擎。请基于用户购物车和历史订单生成个性化商品推荐。
要求输出JSON，字段为AIRecommend对象数组。
score范围0到1，按相关性降序，生成%d个推荐结果（若不足可减少数量）。
用户ID：%d
请优先推荐与购物车商品同类、互补、复购相关的商品。
""".formatted(size, userId);
        RecommendResponse response = llmService.customPromptStructCall(prompt, "请输出商品推荐结果", RecommendResponse.class, aiTools);
        List<UserRecommend> recs = response == null || response.recommendations() == null ? List.of() : response.recommendations().stream()
            .map(r -> {
                UserRecommend rec = new UserRecommend();
                rec.setUserId(userId);
                rec.setProductId(r.productId());
                rec.setScore(r.score());
                rec.setReason(r.reason());
                rec.setCreateTime(new Timestamp(System.currentTimeMillis()));
                return rec;
            }).toList();
        userRecommendMapper.delete(new LambdaQueryWrapper<UserRecommend>().eq(UserRecommend::getUserId, userId));
        userRecommendMapper.insert(recs);
    }

    public RecommendListVO recommendByBehavior(Integer userId, Integer size) {
        LocalDate today = LocalDate.now();
        Timestamp lastRecommendTime = userRecommendMapper.getLastRecommendTime(userId);
        if (lastRecommendTime==null || lastRecommendTime.toLocalDateTime().isBefore(today.atStartOfDay())) {
            updateUserRecommend(userId, 20);
        }
        List<RecommendVO> recommendations = userRecommendMapper.getVOsByUserId(userId).stream()
            .sorted(Comparator.comparing(RecommendVO::getScore, Comparator.nullsLast(Comparator.reverseOrder())))
            .limit(size)
            .toList();
        RecommendListVO vo = new RecommendListVO();
        vo.setRecommendations(recommendations);
        return vo;
    }

    public void refresh(Integer userId, String eventType, Integer productId) {
        // if (userId == null || eventType == null) throw ApiException.err(400, "参数不能为空");
        updateUserRecommend(userId, 20);
    }

    private record AIRecommend(
        @JsonPropertyDescription("商品ID") Integer productId,
        @JsonPropertyDescription("推荐分数，范围从0到1，表示该商品被推荐的可能性") Double score,
        @JsonPropertyDescription("推荐理由") String reason
    ) {}
    private record RecommendResponse(List<AIRecommend> recommendations) {}
}
