package com.whut.emall.ai;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.whut.emall.ai.service.PredictService;
import com.whut.emall.ai.vo.ChurnPredictionVO;
import com.whut.emall.ai.vo.InventoryPredictionVO;
import com.whut.emall.ai.vo.UserProfileVO;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = AIApplication.class)
public class PredictServiceTest {

    @Resource
    private PredictService predictService;

    @Test
    @Order(1)
    public void testPredictInventory() {
        InventoryPredictionVO vo = predictService.predictInventory(1, 7);
        Assertions.assertNotNull(vo);
        Assertions.assertNotNull(vo.getProductId());
        log.info("库存预测：商品={}, 预测销量={}, 当前库存={}",
            vo.getProductName(), vo.getPredictedSales(), vo.getCurrentStock());
    }

    @Test
    @Order(2)
    public void testPredictInventoryDefaultDays() {
        InventoryPredictionVO vo = predictService.predictInventory(1, null);
        Assertions.assertNotNull(vo);
        Assertions.assertNotNull(vo.getForecast());
    }

    @Test
    @Order(3)
    public void testPredictChurn() {
        ChurnPredictionVO vo = predictService.predictChurn(0.7);
        Assertions.assertNotNull(vo);
        Assertions.assertNotNull(vo.getHighRiskUsers());
        log.info("流失预测：高风险用户数={}, 总分析数={}",
            vo.getHighRiskUsers().size(), vo.getTotalAnalyzed());
    }

    @Test
    @Order(4)
    public void testPredictChurnDefaultThreshold() {
        ChurnPredictionVO vo = predictService.predictChurn(null);
        Assertions.assertNotNull(vo);
        Assertions.assertNotNull(vo.getHighRiskUsers());
    }

    @Test
    @Order(5)
    public void testGetUserProfile() throws Exception {
        UserProfileVO vo = predictService.getUserProfile(1);
        Assertions.assertNotNull(vo);
        Assertions.assertNotNull(vo.getUserId());
        Assertions.assertNotNull(vo.getLabels());
        log.info("用户画像：用户ID={}, 标签数={}", vo.getUserId(), vo.getLabels().size());
    }
}
