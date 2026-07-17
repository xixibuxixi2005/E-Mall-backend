package com.whut.emall.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.whut.emall.ai.service.PredictService;
import com.whut.emall.ai.vo.ChurnPredictionVO;
import com.whut.emall.ai.vo.InventoryPredictionVO;
import com.whut.emall.ai.vo.UserProfileVO;
import com.whut.emall.business.entity.Member;
import com.whut.emall.business.entity.Order;
import com.whut.emall.business.entity.OrderItem;
import com.whut.emall.business.entity.Product;
import com.whut.emall.business.entity.enums.MemberLevel;
import com.whut.emall.business.mapper.MemberMapper;
import com.whut.emall.business.mapper.OrderItemMapper;
import com.whut.emall.business.mapper.OrderMapper;
import com.whut.emall.business.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class PredictServiceImpl implements PredictService {
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final MemberMapper memberMapper;
    private final OpenAiChatModel chatModel;



    //--------------1、库存预测
    @Override
    public InventoryPredictionVO predictInventory(Long productId, Integer days)
    {
        //获取商品
        Product product = productMapper.selectById(productId);
        if(product == null)
        {
            throw new RuntimeException("商品不存在");
        }

        //历史销售数据
        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate startDate = endDate.minusDays(30);
        List<OrderItem> history = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getProductId, productId)
                        .ge(OrderItem::getCreateTime, startDate.atStartOfDay())
                        .le(OrderItem::getCreateTime, endDate.atStartOfDay())
        );

        //计算每日销量
        Map<LocalDate, Integer> dailySales = new HashMap<>();
        for(OrderItem orderItem : history)
        {
            LocalDate date =orderItem.getCreateTime().toLocalDateTime().toLocalDate();
            dailySales.merge(date, orderItem.getQuantity(), Integer::sum);
        }

        // 简单预测：计算日均销量 * 天数 + 安全库存
        int totalSales = dailySales.values().stream().mapToInt(Integer::intValue).sum();
        double avgDaily = dailySales.isEmpty() ? 1.0 : (double) totalSales / dailySales.size();
        int predictedSales = (int) Math.ceil(avgDaily * days);
        int currentStock = product.getStock() != null ? product.getStock() : 0;
        int suggestedRestock = Math.max(0, predictedSales + 10 - currentStock);

        //生成预测
        List<InventoryPredictionVO.DailyForecast> forecast = new ArrayList<>();
        for(int i=1;i<= days;i++)
        {
            InventoryPredictionVO.DailyForecast daily = new InventoryPredictionVO.DailyForecast();
            daily.setDate(LocalDate.now().plusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE));
            daily.setSales(String.valueOf((int) Math.ceil(avgDaily)));
            forecast.add(daily);
        }

        //组装响应
        InventoryPredictionVO vo = new InventoryPredictionVO();
        InventoryPredictionVO vo = new InventoryPredictionVO();
        vo.setProductId(productId);
        vo.setProductName(product.getName());
        vo.setPredictedSales(predictedSales);
        vo.setCurrentStock(currentStock);
        vo.setSuggestedRestock(suggestedRestock);
        vo.setConfidenceInterval(Arrays.asList(
                (int) Math.ceil(avgDaily * days * 0.8),
                (int) Math.ceil(avgDaily * days * 1.2)
        ));
        vo.setForecast(forecast);
        return vo;
    }

    // ========== 2. 会员流失预警
    @Override
    public ChurnPredictionVO predictChurn(Double threshold) {
        if(threshold == null) threshold = 0.7;

        //获取会员
        List<Member> mebers = memberMapper.selectList(null);
        List<ChurnPredictionVO.HighRiskUser> highRiskUsers = new ArrayList<>();

        LocalDate now = LocalDate.now();

        for(Member member : mebers)
        {
            //获取最近订单时间
            Order lastOrder = orderMapper.selectOne(
                    new LambdaQueryWrapper<Order>()
                            .eq(Order::getUserId, member.getId())
                            .orderByDesc(Order::getCreateTime)
                            .last("LIMIT 1")
            );

            //计算风险因素
            double riskScore=0.0;
            List<String> reasons = new ArrayList<>();

            // 因素1：最近购买间隔
            if (lastOrder != null) {
                long daysSinceLastOrder = java.time.Duration.between(
                        lastOrder.getCreateTime().toInstant(),
                        now.atStartOfDay().toInstant(java.time.ZoneOffset.UTC)
                ).toDays();
                if (daysSinceLastOrder > 30) {
                    riskScore += 0.3;
                    reasons.add("上次购买间隔超30天");
                }
                if (daysSinceLastOrder > 60) {
                    riskScore += 0.3;
                    reasons.add("上次购买间隔超60天");
                }
            } else {
                // 从未下单的用户
                riskScore += 0.5;
                reasons.add("从未下单");
            }

            // 因素2：登录频率（需要登录日志表，这里简化）
            // 因素3：购买频次
            long orderCount = orderMapper.selectCount(
                    new LambdaQueryWrapper<Order>()
                            .eq(Order::getUserId, member.getId())
            );
            if (orderCount < 2) {
                riskScore += 0.2;
                reasons.add("购买次数少");
            }

            // 4. 高风险管理
            if (riskScore >= threshold) {
                ChurnPredictionVO.HighRiskUser user = new ChurnPredictionVO.HighRiskUser();
                user.setUserId(Long.valueOf(member.getId()));
                user.setRiskScore(Math.min(riskScore, 1.0));
                user.setReason(String.join("、", reasons));
                highRiskUsers.add(user);
            }
        }

        // 5. 按风险分数降序排列
        highRiskUsers.sort((a, b) -> b.getRiskScore().compareTo(a.getRiskScore()));

        ChurnPredictionVO vo = new ChurnPredictionVO();
        vo.setHighRiskUsers(highRiskUsers);
        vo.setTotalAnalyzed(mebers.size());
        return vo;
        }


    // ========== 3. 会员画像标签
    @Override
    public UserProfileVO getUserProfile(Long userId) {
        // 获取用户信息
        Member member = memberMapper.selectById(userId);
        if (member == null) {
            throw new RuntimeException("用户不存在");
        }

        // 获取用户订单统计
        List<Order> orders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, userId)
        );

        // 计算统计数据
        int orderCount = orders.size();
        BigDecimal totalAmount = orders.stream()
                .map(Order::getPayAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 生成标签
        List<UserProfileVO.Label> labels = new ArrayList<>();

        // 消费能力标签
        if (totalAmount.compareTo(new BigDecimal("10000")) > 0) {
            labels.add(createLabel("高消费用户", 0.9));
        } else if (totalAmount.compareTo(new BigDecimal("5000")) > 0) {
            labels.add(createLabel("中等消费用户", 0.8));
        } else if (orderCount > 0) {
            labels.add(createLabel("普通消费者", 0.7));
        }

        // 购买频次标签
        if (orderCount > 10) {
            labels.add(createLabel("高频购买", 0.85));
        } else if (orderCount > 3) {
            labels.add(createLabel("活跃用户", 0.75));
        } else if (orderCount > 0) {
            labels.add(createLabel("新用户", 0.6));
        }

        //会员等级标签
        if (member.getLevel() != null &&
                (member.getLevel() == MemberLevel.SIVLER || member.getLevel() == MemberLevel.GOLD)) {
            labels.add(createLabel("高级会员", 0.9));
        }

        // 组装响应
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
