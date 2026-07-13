package com.whut.emall.business.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.business.config.EMallResponse;
import com.whut.emall.business.entity.Order;
import com.whut.emall.business.entity.enums.OrderStatus;
import com.whut.emall.business.service.OrderService;
import com.whut.emall.business.vo.OrderDetailListVO;
import com.whut.emall.business.vo.OrderDetailVO;
import com.whut.emall.business.vo.OrderListVO;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.entity.ApiResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/biz/order")
@EMallResponse
@Tag(name = "订单接口", description = "查看订单详情")
public class OrderController {
    @Resource OrderService orderService;

    @Operation(summary = "订单列表", description = "分页查询订单列表，可按订单号、用户ID、订单状态、起始时间和结束时间进行筛选")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("list")
    public ApiResult<OrderListVO> getOrderList(
        @Parameter(hidden = true) @RequestHeader("X-Role") String role,
        @RequestParam(required = false, defaultValue = "1") Integer pageNum,
        @RequestParam(required = false, defaultValue = "10") Integer pageSize,
        @RequestParam(required = false) String orderNo,
        @RequestParam(required = false) Integer userId,
        @RequestParam(required = false) OrderStatus status,
        @RequestParam(required = false) Timestamp startTime,
        @RequestParam(required = false) Timestamp endTime
    ) {
        if (!"ADMIN".equals(role) && !"CS".equals(role))
            throw ApiException.err(403, "无权限查看订单列表");
        return ApiResult.ok("查询成功", orderService.orderList(pageNum, pageSize, orderNo, userId, status, startTime, endTime));
    }
    
    @Operation(summary = "订单详情", description = "获取订单的具体内容")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("{id}")
    public ApiResult<OrderDetailVO> getOrderDetail(
        @Parameter(hidden = true) @RequestHeader("X-Role") String role,
        @Parameter(hidden = true) @RequestHeader("X-User-Id") Integer uid,
        @PathVariable Integer id
    ) {
        if ("ADMIN".equals(role) || "CS".equals(role)) uid = null;
        return ApiResult.ok("查询成功", orderService.orderDetail(uid, id));
    }
    
    @Operation(summary = "订单状态更新", description = "更新订单状态")
    @ApiResponse(responseCode = "200", description = "状态更新成功")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("{id}/status")
    public ApiResult<Void> updateOrderStatus(
        @Parameter(hidden = true) @RequestHeader("X-Role") String role,
        @PathVariable Integer id,
        @RequestBody @Valid OrderStatusDTO dto
    ) {
        if (!"ADMIN".equals(role))
            throw ApiException.err(403, "无权限更新订单状态");
        orderService.updateStatus(id, dto.getStatus(), dto.getShippingNo());
        return ApiResult.ok("状态更新成功");
    }
    
    @Operation(summary = "创建订单", description = "普通用户提交订单")
    @ApiResponse(responseCode = "200", description = "订单创建成功，请完成支付")
    @SecurityRequirement(name = "Authorization")
    @PostMapping
    public ApiResult<Order> createOrder(
        @Parameter(hidden = true) @RequestHeader("X-User-Id") Integer uid,
        @RequestBody @Valid OrderCreateDTO dto
    ) {
        Order order = orderService.createOrder(uid, dto.getCartIds(), dto.getReceiverName(), dto.getReceiverPhone(), dto.getReceiverAddress(), dto.getRemark());
        return ApiResult.ok("订单创建成功，请完成支付", order);
    }
    
    @Operation(summary = "个人订单查询", description = "查询用户自身的订单")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("/my/list")
    public ApiResult<OrderDetailListVO> myOrders(
        @Parameter(hidden = true) @RequestHeader("X-User-Id") Integer uid,
        @RequestParam(required = false, defaultValue = "1") Integer pageNum,
        @RequestParam(required = false, defaultValue = "10") Integer pageSize,
        @RequestParam(required = false) OrderStatus status
    ) {
        return ApiResult.ok("查询成功", orderService.myOrders(pageNum, pageSize, uid, status));
    }
    
    @Operation(summary = "取消订单", description = "普通用户取消自己的订单（仅限待支付状态）")
    @ApiResponse(responseCode = "200", description = "订单已取消")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("{id}/cancel")
    public ApiResult<Void> cancelOrder(
        @Parameter(hidden = true) @RequestHeader("X-User-Id") Integer uid,
        @PathVariable Integer id,
        @RequestBody @Valid OrderCancelDTO dto
    ) {
        orderService.cancel(uid, id, dto.getReason());
        return ApiResult.ok("订单已取消");
    }

    @Data
    static class OrderStatusDTO {
        @NotNull(message = "status 不可为空") OrderStatus status;
        String shippingNo;
    }

    @Data
    static class OrderCreateDTO {
        @NotEmpty(message = "至少选择一个商品")
        List<Integer> cartIds;
        @NotEmpty(message = "收货人不可为空")
        String receiverName;
        @NotEmpty(message = "收货人电话号码不可为空")
        String receiverPhone;
        @NotEmpty(message = "收货地址不可为空")
        String receiverAddress;
        String remark;
    }

    @Data
    static class OrderCancelDTO {
        String reason;
    }
}
