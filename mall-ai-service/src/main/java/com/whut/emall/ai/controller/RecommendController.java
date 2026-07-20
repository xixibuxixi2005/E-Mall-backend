package com.whut.emall.ai.controller;

import com.whut.emall.ai.service.RecommendService;
import com.whut.emall.ai.vo.RecommendListVO;
import com.whut.emall.common.entity.ApiResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommend")
@Tag(name = "推荐接口", description = "提供商品推荐功能")
public class RecommendController {
    @Resource RecommendService recommendService;

    @Operation(summary = "获取推荐列表", description = "根据用户购物车和历史订单生成个性化推荐列表，包含推荐理由")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @SecurityRequirement(name = "Authorization")
    @PostMapping
    public ApiResult<RecommendListVO> getUserRecommend(
        @Parameter(hidden = true) @RequestHeader("X-User-Id") Integer userId,
        @RequestBody @Valid RecommendRequest request
    ) throws Exception {
        return ApiResult.ok("获取成功", recommendService.recommendByBehavior(userId, request.getSize()));
    }

    @Operation(summary = "触发推荐刷新", description = "提交一次用户行为事件，用于后续推荐更新")
    @ApiResponse(responseCode = "200", description = "刷新任务已提交")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("/refresh")
    public ApiResult<Void> refresh(
        @Parameter(hidden = true) @RequestHeader("X-User-Id") Integer userId,
        @RequestBody @Valid RefreshRequest request
    ) {
        recommendService.refresh(userId, request.getEventType(), request.getProductId());
        return ApiResult.ok("刷新任务已提交");
    }
    
    @Data
    static class RecommendRequest {
        @Min(1) @Max(20) Integer size = 6;
    }

    @Data
    static class RefreshRequest {
        @NotBlank String eventType;
        @NotNull Integer productId;
    }
}
/*
3.2 触发推荐刷新（异步）
POST /api/ai/recommend/refresh
权限：内部调用（或管理员手动触发）

请求体：
{
  "userId": 1001,
  "eventType": "VIEW",   // VIEW / PURCHASE / FAVORITE
  "productId": 1003
}
响应：{ "code": 200, "msg": "刷新任务已提交" }
该接口通常由业务服务通过RabbitMQ事件触发，也可暴露给前端在用户行为后调用（需限流）。
 */