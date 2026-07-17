package com.whut.emall.ai.service;

import com.whut.emall.ai.vo.ChurnPredictionVO;
import com.whut.emall.ai.vo.InventoryPredictionVO;
import com.whut.emall.ai.vo.UserProfileVO;

public interface PredictService {
    /**
     * 库存预测
     * 基于历史销售数据预测未来销售，生成补货建议
     */
    InventoryPredictionVO predictInventory(Long productId, Integer days);

    /**
     * 会员流失预警
     * 分析用户行为，识别高风险会员
     */
    ChurnPredictionVO predictChurn(Double threshold);

    /**
     * 会员画像标签
     * 基于用户行为生成标签
     */
    UserProfileVO getUserProfile(Long userId);
}

