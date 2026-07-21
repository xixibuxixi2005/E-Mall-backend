package com.whut.emall.ai.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.whut.emall.ai.vo.ChurnPredictionVO;
import com.whut.emall.ai.vo.InventoryPredictionVO;
import com.whut.emall.ai.vo.UserProfileVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whut.emall.ai.tools.AITools;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.vo.CartListVO;
import com.whut.emall.common.vo.MemberInfo;
import com.whut.emall.common.vo.OrderDetailListVO;
import com.whut.emall.common.vo.ProductDetailVO;

import jakarta.annotation.Resource;

@Service
public class PredictService {
    @Resource LLMService llmService;
    @Resource AITools aiTools;
    final ObjectMapper objectMapper = new ObjectMapper();

    public InventoryPredictionVO predictInventory(Integer productId, Integer days) {
        int forecastDays = days == null ? 7 : Math.max(1, days);
        ProductDetailVO product = aiTools.getProductDetail(productId);
        if (product == null) {
            throw ApiException.err(404, "商品不存在");
        }

        String systemPrompt = "你是电商库存预测AI。请结合商品信息、商品列表、分类信息与用户历史订单趋势，输出库存预测结果。"
            + "要求："
            + "1) predictedSales 为未来指定天数的预测总销量；"
            + "2) currentStock 为当前库存；"
            + "3) suggestedRestock 为建议补货量；"
            + "4) confidenceInterval 为长度2的数组，表示保守与乐观预测区间；"
            + "5) forecast 为按天输出的销售趋势；"
            + "6) 保持 JSON 结构稳定。";
        String question = "商品ID=" + productId + ", 预测天数=" + forecastDays;

        InventoryPredictionVO result = llmService.customPromptStructCall(
            systemPrompt,
            question,
            InventoryPredictionVO.class,
            aiTools
        );

        if (result == null) {
            result = new InventoryPredictionVO();
        }
        result.setProductId(productId);
        result.setProductName(product.getName());
        if (result.getCurrentStock() == null) {
            result.setCurrentStock(product.getStock());
        }
        if (result.getForecast() == null) {
            result.setForecast(List.of());
        }
        if (result.getConfidenceInterval() == null || result.getConfidenceInterval().size() < 2) {
            result.setConfidenceInterval(List.of(0, 0));
        }
        return result;
    }

    public ChurnPredictionVO predictChurn(Double threshold) {
        double riskThreshold = threshold == null ? 0.7 : threshold;
        OrderDetailListVO orders = aiTools.getMyOrders(1, 100, null, null);
        String systemPrompt = "你是电商会员流失分析AI。请结合会员信息、订单信息、购物车信息输出流失风险分析。"
            + "要求："
            + "1) highRiskUsers 为高风险用户列表；"
            + "2) 每个用户包含 userId、riskScore、reason；"
            + "3) riskScore 为 0 到 1 的小数；"
            + "4) 如果信息不足，基于订单活跃度、支付金额、最近消费频率做合理推断。";
        String question = "阈值=" + riskThreshold;

        ChurnPredictionVO result = llmService.customPromptStructCall(
            systemPrompt,
            question,
            ChurnPredictionVO.class,
            (Object) aiTools
        );

        if (result == null) {
            result = new ChurnPredictionVO();
        }
        if (result.getHighRiskUsers() == null) {
            result.setHighRiskUsers(List.of());
        }
        result.setTotalAnalyzed(orders == null || orders.getList() == null ? 0 : orders.getList().size());
        return result;
    }

    @Cacheable(value = "EMALL:AI:USER_PROFILE", key = "#userId")
    public UserProfileVO getUserProfile(Integer userId) throws Exception{
        MemberInfo member = aiTools.getMemberInfo(userId);
        OrderDetailListVO orders = aiTools.getMyOrders(userId, 1, -1, null);
        CartListVO cart = aiTools.getCartList(userId);

        String systemPrompt = """
你是电商会员画像AI。请结合会员资料、订单和购物车信息，以JSON格式输出用户画像标签。
格式如{"userId":1,"labels":[{"name":"高消费用户","confidence":0.8},{"name":"数码迷","confidence":0.75}]}
labels 是标签数组，每个标签包含标签名称name和标签置信度confidence；
标签要体现消费能力、购买频次、品类偏好、价格敏感度、活跃程度等特征；"
""";
        String question = "会员信息=" + objectMapper.writeValueAsString(member) + ", 订单数据=" + objectMapper.writeValueAsString(orders) + ", 购物车数据=" + objectMapper.writeValueAsString(cart);

        UserProfileVO result = llmService.customPromptStructCall(
            systemPrompt,
            question,
            UserProfileVO.class,
            (Object) aiTools
        );

        if (result == null) {
            result = new UserProfileVO();
        }
        result.setUserId(userId);
        if (result.getLabels() == null) {
            result.setLabels(List.of());
        }
        return result;
    }

}
