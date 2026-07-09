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

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@RestController
@RequestMapping("/biz/cart")
public class CartController {
    @Resource CartService cartService;

    @PostMapping("/add")
    public ApiResult add(@RequestHeader("X-User-Id") Integer userId, @RequestBody @Valid CartAddDTO dto) {
        return ApiResult.ok("添加成功", cartService.add(userId, dto.getProductId(), dto.getQuantity()));
    }

    @GetMapping("/list")
    public ApiResult list(@RequestHeader("X-User-Id") Integer userId) {
        return ApiResult.ok("操作成功", cartService.list(userId));
    }

    @PutMapping("/update")
    public ApiResult update(@RequestHeader("X-User-Id") Integer userId, @RequestBody @Valid CartUpdateDTO dto) {
        cartService.update(userId, dto.getCartId(), dto.getQuantity());
        return ApiResult.ok("更新成功");
    }

    @DeleteMapping("/remove")
    public ApiResult remove(@RequestHeader("X-User-Id") Integer userId, @RequestBody @Valid CartRemoveDTO dto) {
        cartService.remove(userId, dto.getCartIds());
        return ApiResult.ok("删除成功");
    }

    @DeleteMapping("/clear")
    public ApiResult clear(@RequestHeader("X-User-Id") Integer userId) {
        cartService.clear(userId);
        return ApiResult.ok("已清空购物车");
    }

    @PutMapping("/select")
    public ApiResult select(@RequestHeader("X-User-Id") Integer userId, @RequestBody @Valid CartSelectDTO dto) {
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
