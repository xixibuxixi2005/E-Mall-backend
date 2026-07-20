package com.whut.emall.business.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.business.service.CartService;
import com.whut.emall.common.entity.ApiResult;
import com.whut.emall.common.vo.CartDetailVO;
import com.whut.emall.common.vo.CartListVO;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "购物车接口", description = "购物车增删改查与选择状态管理")
@RestController
@RequestMapping("/biz/cart")
public class CartController {
    @Resource CartService cartService;

    @Operation(summary = "加入购物车", description = "向当前用户购物车中添加商品")
    @ApiResponse(responseCode = "200", description = "添加成功")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("/add")
    public ApiResult<CartDetailVO> add(@Parameter(hidden = true) @RequestHeader("X-User-Id") Integer userId, @RequestBody @Valid CartAddDTO dto) {
        return ApiResult.ok("添加成功", cartService.add(userId, dto.getProductId(), dto.getQuantity()));
    }

    @Operation(summary = "购物车列表", description = "获取当前用户购物车明细")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("/list")
    public ApiResult<CartListVO> list(@Parameter(hidden = true) @RequestHeader("X-User-Id") Integer userId) {
        return ApiResult.ok("操作成功", cartService.list(userId));
    }

    @Operation(summary = "更新购物车商品数量", description = "修改购物车条目的购买数量")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("/update")
    public ApiResult<Void> update(@Parameter(hidden = true) @RequestHeader("X-User-Id") Integer userId, @RequestBody @Valid CartUpdateDTO dto) {
        cartService.update(userId, dto.getCartId(), dto.getQuantity());
        return ApiResult.ok("更新成功");
    }

    @Operation(summary = "删除购物车条目", description = "批量删除购物车记录")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("/remove")
    public ApiResult<Void> remove(@Parameter(hidden = true) @RequestHeader("X-User-Id") Integer userId, @RequestBody @Valid CartRemoveDTO dto) {
        cartService.remove(userId, dto.getCartIds());
        return ApiResult.ok("删除成功");
    }

    @Operation(summary = "清空购物车", description = "删除当前用户的全部购物车记录")
    @ApiResponse(responseCode = "200", description = "清空成功")
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("/clear")
    public ApiResult<Void> clear(@Parameter(hidden = true) @RequestHeader("X-User-Id") Integer userId) {
        cartService.clear(userId);
        return ApiResult.ok("已清空购物车");
    }

    @Operation(summary = "设置勾选状态", description = "修改购物车条目的选中状态")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("/select")
    public ApiResult<Void> select(@Parameter(hidden = true) @RequestHeader("X-User-Id") Integer userId, @RequestBody @Valid CartSelectDTO dto) {
        cartService.select(userId, dto.getCartId(), dto.getSelected());
        return ApiResult.ok("更新成功");
    }

    @Data
    static class CartAddDTO {
        @NotNull(message = "商品ID不能为空")
        Integer productId;
        @NotNull(message = "购买数量不能为空")
        @Min(value = 1, message = "购买数量不能小于1")
        @Max(value = 99, message = "购买数量不能大于99")
        Integer quantity;
    }

    @Data
    static class CartUpdateDTO {
        @NotNull(message = "购物车ID不能为空")
        Integer cartId;
        @NotNull(message = "购买数量不能为空")
        @Min(value = 1, message = "购买数量不能小于1")
        @Max(value = 99, message = "购买数量不能大于99")
        Integer quantity;
    }

    @Data
    static class CartRemoveDTO {
        @NotNull(message = "购物车ID列表不能为空")
        List<Integer> cartIds;
    }

    @Data
    static class CartSelectDTO {
        @NotNull(message = "购物车ID不能为空")
        Integer cartId;
        @NotNull(message = "选中状态不能为空")
        Boolean selected;
    }
}
