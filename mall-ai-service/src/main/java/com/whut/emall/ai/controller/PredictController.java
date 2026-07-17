package com.whut.emall.ai.controller;

import com.whut.emall.ai.service.PredictService;
import com.whut.emall.ai.vo.InventoryPredictionVO;
import com.whut.emall.ai.vo.ChurnPredictionVO;
import com.whut.emall.ai.vo.UserProfileVO;
import com.whut.emall.common.entity.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/predict")
@RequiredArgsConstructor
@Tag(name = "AI辅助决策与预警", description = "库存预测、会员流失预警、会员画像标签")
@SecurityRequirement(name = "Authorization")
public class PredictController {

    private final PredictService predictService;

    @Operation(summary = "库存预测（补货建议）",
            description = "基于历史销售数据预测未来销量，生成补货建议")
    @GetMapping("/inventory")
    public ApiResult<InventoryPredictionVO> predictInventory(
            @RequestParam Integer productId,
            @RequestParam(required = false, defaultValue = "7") Integer days) {
        return ApiResult.ok("预测成功", predictService.predictInventory(productId, days));
    }

    @Operation(summary = "会员流失预警",
            description = "分析用户行为，识别高流失风险会员")
    @GetMapping("/churn")
    public ApiResult<ChurnPredictionVO> predictChurn(
            @RequestParam(required = false, defaultValue = "0.7") Double threshold) {
        return ApiResult.ok("分析成功", predictService.predictChurn(threshold));
    }
}