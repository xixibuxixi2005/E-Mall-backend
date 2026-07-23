package com.whut.emall.ai;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.whut.emall.ai.service.SentimentService;
import com.whut.emall.ai.vo.BatchSentimentVO;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = AIApplication.class)
public class SentimentServiceTest {

    @Resource
    private SentimentService sentimentService;

    @Test
    @Order(1)
    public void testAnalyzeSingle() {
        var result = sentimentService.analyze("商品质量很好，物流也很快，非常满意！");
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.sentiment());
        log.info("情感分析结果：type={}, score={}", result.sentiment(), result.score());
    }

    @Test
    @Order(2)
    public void testAnalyzePositive() {
        var result = sentimentService.analyze("这款手机性能强劲，拍照清晰，续航也很棒！");
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.keywords());
    }

    @Test
    @Order(3)
    public void testAnalyzeNegative() {
        var result = sentimentService.analyze("商品质量很差，物流慢，客服态度也不好，非常失望！");
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.sentiment());
    }

    @Test
    @Order(4)
    public void testAnalyzeBatch() {
        List<String> comments = List.of(
            "商品质量很好，非常满意！",
            "物流太慢了，等了很久才收到",
            "一般般吧，没想象中好"
        );
        BatchSentimentVO vo = sentimentService.analyze(comments);
        Assertions.assertNotNull(vo);
        Assertions.assertNotNull(vo.getSentiments());
        log.info("批量分析结果：正面={}, 中性={}, 负面={}", vo.getPositiveCount(), vo.getNeutralCount(), vo.getNegativeCount());
    }

    @Test
    @Order(5)
    public void testAnalyzeEmptyList() {
        List<String> comments = List.of("", "  ", "");
        BatchSentimentVO vo = sentimentService.analyze(comments);
        Assertions.assertNotNull(vo);
    }
}
