package com.whut.emall.business.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.business.config.EMallResponse;
import com.whut.emall.business.entity.enums.ProductStatus;
import com.whut.emall.business.service.ProductService;
import com.whut.emall.business.vo.ProductDetailVO;
import com.whut.emall.business.vo.ProductListVO;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.entity.ApiResult;

import jakarta.annotation.Resource;
import lombok.Data;

import org.springframework.web.bind.annotation.RequestParam;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;



@Tag(name = "商品接口", description = "商品详情查询")
@RestController
@RequestMapping("/biz/product")
@EMallResponse
public class ProductController {
    @Resource ProductService productService;

    @Operation(summary = "商品列表", description = "分页查询商品列表，可按名称、状态和价格区间筛选")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @SecurityRequirement(name = "bearerAuth")
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

    @Operation(summary = "新增商品", description = "创建新商品并返回商品 ID")
    @ApiResponse(responseCode = "200", description = "新增成功")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ApiResult<Map<String,Integer>> createProduct(
            @RequestHeader("X-Role") String role,
            @RequestBody ProductDTO product
        ) {
        if (!"ADMIN".equals(role)) {
            throw ApiException.err(403, "无权限添加商品");
        }
        int pid = productService.createProduct(
            product.getName(),
            product.getSubtitle(),
            product.getDescription(),
            product.getCategoryId(),
            product.getPrice(),
            product.getOriginalPrice(),
            product.getStock(),
            product.getImageUrls()
        );
        return ApiResult.ok("新增成功", Map.of("id", pid));
    }

    @Operation(summary = "获取商品详情", description = "根据商品 ID 获取商品详情信息")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("{id}")
    public ApiResult<ProductDetailVO> getProductDetail(@PathVariable Integer id) {
        return ApiResult.ok("操作成功", productService.getProductById(id));
    }

    @Data
    static class ProductDTO {
        String name;
        String subtitle;
        Integer categoryId;
        String description;
        BigDecimal price;
        BigDecimal originalPrice;
        Integer stock;
        List<String> imageUrls;
    }
}
