package com.whut.emall.business.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.business.service.ProductService;
import com.whut.emall.common.entity.ApiResult;

import jakarta.annotation.Resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "商品接口", description = "商品详情查询")
@RestController
@RequestMapping("/biz/product")
public class ProductController {
    @Resource ProductService productService;

    @Operation(summary = "获取商品详情", description = "根据商品 ID 获取商品详情信息")
    @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = ApiResult.class)))
    @GetMapping("{id}")
    public ApiResult getProductDetail(@PathVariable Integer id) {
        return ApiResult.ok("操作成功", productService.getProductById(id));
    }
}
