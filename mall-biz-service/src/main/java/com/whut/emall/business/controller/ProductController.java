package com.whut.emall.business.controller;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.business.config.EMallResponse;
import com.whut.emall.business.entity.Product;
import com.whut.emall.business.entity.enums.ProductStatus;
import com.whut.emall.business.service.ProductService;
import com.whut.emall.business.vo.ProductDetailVO;
import com.whut.emall.business.vo.ProductListVO;
import com.whut.emall.common.entity.ApiResult;

import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.RequestParam;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Tag(name = "商品接口", description = "商品详情查询")
@RestController
@RequestMapping("/biz/product")
@EMallResponse
public class ProductController {
    @Resource ProductService productService;

    @Operation(summary = "商品列表", description = "分页查询商品列表，可按名称、状态和价格区间筛选")
    @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = ApiResult.class)))
    @GetMapping("list")
    public ApiResult<ProductListVO> list(
        @RequestParam(required = false, defaultValue = "1") Integer pageNum,
        @RequestParam(required = false, defaultValue = "10") Integer pageSize,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) ProductStatus status,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return ApiResult.ok("查询成功", productService.productlist(pageNum, pageSize, name, status, minPrice, maxPrice));
    }

    // @PostMapping
    // public ApiResult createProduct(@RequestBody Product product) {
    //     return ApiResult.ok("创建成功", productService.createProduct(product));
    // }

    @Operation(summary = "获取商品详情", description = "根据商品 ID 获取商品详情信息")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping("{id}")
    public ApiResult<ProductDetailVO> getProductDetail(@PathVariable Integer id) {
        return ApiResult.ok("操作成功", productService.getProductById(id));
    }
}
