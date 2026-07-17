package com.whut.emall.ai.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.whut.emall.ai.client.BizClient;
import com.whut.emall.ai.mapper.OrderItemMapper;
import com.whut.emall.ai.vo.ChurnPredictionVO;
import com.whut.emall.ai.vo.InventoryPredictionVO;
import com.whut.emall.ai.vo.UserProfileVO;
import com.whut.emall.common.entity.OrderItem;
import com.whut.emall.common.entity.enums.MemberLevel;
import com.whut.emall.common.vo.MemberInfo;
import com.whut.emall.common.vo.OrderDetailListVO;
import com.whut.emall.common.vo.OrderDetailVO;
import com.whut.emall.common.vo.ProductDetailVO;

import jakarta.annotation.Resource;

@Service
public class PredictService {
    @Resource BizClient bizClient;
    @Resource OrderItemMapper orderItemMapper;

    public InventoryPredictionVO predictInventory(Integer productId, Integer days) {
        days = Math.max(1, days);
        //获取商品详情
        ProductDetailVO product = bizClient.getProductDetail(productId).getData();

        //历史销售数据
        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate startDate = endDate.minusDays(30);
        List<OrderItem> history = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
            .eq(OrderItem::getProductId, productId)
            .ge(OrderItem::getCreateTime, startDate)
            .le(OrderItem::getCreateTime, endDate));
        
        //计算每日销量
        Map<LocalDate, Integer> dailySales = new HashMap<>();
        for (OrderItem orderItem : history) {
            dailySales.merge(orderItem.getCreateTime().toLocalDateTime().toLocalDate(), orderItem.getQuantity(), Integer::sum);
        }

        // 简单预测：计算日均销量 * 天数 + 安全库存
        int totalSales = dailySales.values().stream().mapToInt(Integer::intValue).sum();
        double avgDaily = dailySales.isEmpty() ? 1.0 : (double) totalSales / dailySales.size();
        int predictedSales = (int) Math.ceil(avgDaily * days);
        int currentStock = product.getStock();
        int suggestedRestock = Math.max(0, predictedSales + 10 - currentStock);

        //生成预测
        List<InventoryPredictionVO.DailyForecast> forecast = new ArrayList<>();
        for (int i = 1; i <= days; i++) {
            InventoryPredictionVO.DailyForecast daily = new InventoryPredictionVO.DailyForecast();
            daily.setDate(LocalDate.now().plusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE));
            daily.setSales(String.valueOf((int) Math.ceil(avgDaily)));
            forecast.add(daily);
        }

        //组装响应
        InventoryPredictionVO vo = new InventoryPredictionVO();
        vo.setProductId(productId);
        vo.setProductName(product.getName());
        vo.setPredictedSales(predictedSales);
        vo.setCurrentStock(currentStock);
        vo.setSuggestedRestock(suggestedRestock);
        vo.setConfidenceInterval(List.of(
            (int) Math.ceil(avgDaily * days * 0.8),
            (int) Math.ceil(avgDaily * days * 1.2)
        ));
        vo.setForecast(forecast);
        return vo;
    }

    public ChurnPredictionVO predictChurn(Double threshold) {
        if (threshold == null) threshold = 0.7;
        List<ChurnPredictionVO.HighRiskUser> highRiskUsers = new ArrayList<>();
        ChurnPredictionVO vo = new ChurnPredictionVO();
        vo.setHighRiskUsers(highRiskUsers);
        vo.setTotalAnalyzed(0);
        return vo;
    }

    public UserProfileVO getUserProfile(Integer userId) {
        MemberInfo member = bizClient.getMemberInfo(userId).getData();
        OrderDetailListVO orders = bizClient.myOrders(userId.intValue(), 1, 100, null).getData();
        int orderCount = orders.getList().size();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderDetailVO order : orders.getList()) {
            totalAmount = totalAmount.add(order.getPayAmount());
        }

        List<UserProfileVO.Label> labels = new ArrayList<>();
        if (totalAmount.compareTo(new BigDecimal("10000")) > 0) labels.add(createLabel("高消费用户", 0.9));
        else if (totalAmount.compareTo(new BigDecimal("5000")) > 0) labels.add(createLabel("中等消费用户", 0.8));
        else if (orderCount > 0) labels.add(createLabel("普通消费者", 0.7));

        if (orderCount > 10) labels.add(createLabel("高频购买", 0.85));
        else if (orderCount > 3) labels.add(createLabel("活跃用户", 0.75));
        else if (orderCount > 0) labels.add(createLabel("新用户", 0.6));

        MemberLevel level = member.getLevel();
        switch (level) {
            case NORMAL: labels.add(createLabel(level.getDesc(), 0.6)); break;
            case SILVER: labels.add(createLabel(level.getDesc(), 0.8)); break;
            case GOLD: labels.add(createLabel(level.getDesc(), 0.9)); break;
            default: break;
        }

        UserProfileVO vo = new UserProfileVO();
        vo.setUserId(userId);
        vo.setLabels(labels);
        return vo;
    }

    private UserProfileVO.Label createLabel(String name, double confidence) {
        UserProfileVO.Label label = new UserProfileVO.Label();
        label.setName(name);
        label.setConfidence(confidence);
        return label;
    }
}
