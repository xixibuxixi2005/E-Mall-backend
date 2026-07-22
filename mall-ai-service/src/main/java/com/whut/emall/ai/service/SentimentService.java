package com.whut.emall.ai.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.whut.emall.ai.entity.CommentSentiment;
import com.whut.emall.ai.entity.enums.SentimentType;
import com.whut.emall.ai.vo.BatchSentimentVO;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SentimentService {
    @Resource LLMService llmService;
    @Resource RedisTemplate<String, CommentSentiment> sentimentRedisTemplate;

    private static final String SENTIMENT_CACHE_KEY = "EMALL:AI:SENTIMENT";
    public CommentSentiment analyze(String comment) {
        CommentSentiment cached = sentimentRedisTemplate.opsForValue().get(SENTIMENT_CACHE_KEY + ":" + comment);
        if (cached != null) {
            return cached;
        }
        String prompt = """
你是一个评论情感分析助理，分析用户评论的情感类型和关键词。请根据以下要求进行分析并以JSON格式返回sentiment字段必须为中文：负面、中性、正面。
示例："手机性能很强，就是价格有点贵，但物流很快。"
响应示例：{"sentiment": "正面", "score": 0.75, "keywords": ["性能强", "价格贵", "物流快"], "positiveWords": ["性能强", "物流快"], "negativeWords": ["价格贵"]}
        """;
        CommentSentiment result = llmService.customPromptStructCall(prompt, comment, CommentSentiment.class);
        sentimentRedisTemplate.opsForValue().set(SENTIMENT_CACHE_KEY + ":" + comment, result, Duration.ofDays(7));
        return result;
    }
    
    public BatchSentimentVO analyze(List<String> comment) {
        comment = comment.stream().map(c -> c.strip()).filter(c -> !c.isEmpty()).toList();
        List<String> uncached = new ArrayList<>();
        for (String c : comment) {
            CommentSentiment cached = sentimentRedisTemplate.opsForValue().get(SENTIMENT_CACHE_KEY + ":" + c);
            if (cached == null) {
                uncached.add(c);
            }
        }
        if (!uncached.isEmpty()) {
            String prompt = """
你是一个评论情感分析助理，分析用户评论的情感类型和关键词。请分析每一条评论并以json数组形式返回sentiment字段必须为中文：负面、中性、正面。
示例：["手机性能很强，就是价格有点贵，但物流很快。"]
响应示例：{"sentiments": [{"sentiment": "正面", "score": 0.75, "keywords": ["性能强", "价格贵", "物流快"], "positiveWords": ["性能强", "物流快"], "negativeWords": ["价格贵"]}]}
""";
            StringBuilder userInputs = new StringBuilder();
            for (int i=0 ; i<uncached.size() ; ++i) {
                userInputs.append(String.format("评论%d：%s\n", i+1, uncached.get(i)));
            }
            var result = llmService.customPromptStructCall(prompt, userInputs.toString(), ListSentimentVO.class);
            for (int i=0 ; i<result.sentiments().size() ; ++i) {
                sentimentRedisTemplate.opsForValue().set(SENTIMENT_CACHE_KEY + ":" + uncached.get(i), result.sentiments().get(i), Duration.ofDays(7));
            }
        }
        List<CommentSentiment> cachedList = new ArrayList<>();
        int positive = 0, negative = 0, neutral = 0;
        for (String c : comment) {
            CommentSentiment cached = sentimentRedisTemplate.opsForValue().get(SENTIMENT_CACHE_KEY + ":" + c);
            if (cached != null) {
                cachedList.add(cached);
                if (cached.sentiment() == SentimentType.POSITIVE) {
                    positive++;
                } else if (cached.sentiment() == SentimentType.NEGATIVE) {
                    negative++;
                } else {
                    neutral++;
                }
            }
        }
        return new BatchSentimentVO(cachedList, positive, neutral, negative);
    }

    public record ListSentimentVO(List<CommentSentiment> sentiments) {}
}