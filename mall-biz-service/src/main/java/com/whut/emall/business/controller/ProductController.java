package com.whut.emall.business.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;

import com.whut.emall.business.entity.enums.ProductStatus;
import com.whut.emall.business.service.ProductService;
import com.whut.emall.business.vo.ProductDetailVO;
import com.whut.emall.business.vo.ProductListVO;
import com.whut.emall.common.annotation.EMallResponse;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.entity.ApiResult;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import org.springframework.web.bind.annotation.RequestParam;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @SecurityRequirement(name = "Authorization")
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
    @SecurityRequirement(name = "Authorization")
    @PostMapping
    public ApiResult<Map<String,Integer>> createProduct(
            @Parameter(hidden = true) @RequestHeader("X-Role") String role,
            @RequestBody @Valid ProductDTO product
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
    @SecurityRequirement(name = "Authorization")
    @GetMapping("{id}")
    public ApiResult<ProductDetailVO> getProductDetail(@PathVariable Integer id) {
        return ApiResult.ok("操作成功", productService.getProductById(id));
    }

    @Operation(summary = "更新商品", description = "更新现有商品信息")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("{id}")
    public ApiResult<Void> updateProduct(
            @Parameter(hidden = true) @RequestHeader("X-Role") String role,
            @PathVariable Integer id,
            @RequestBody @Valid UpdateProductDTO product
        ) {
        if (!"ADMIN".equals(role)) {
            throw ApiException.err(403, "无权限更新商品");
        }
        productService.updateProduct(id, product.getName(), product.getSubTitle(), product.getPrice(), product.getStock(), product.getDescription());
        return ApiResult.ok("更新成功");
    }

    @Operation(summary = "删除商品", description = "删除指定商品")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("{id}")
    public ApiResult<Void> deleteProduct(
            @Parameter(hidden = true) @RequestHeader("X-Role") String role,
            @PathVariable Integer id
        ) {
        if (!"ADMIN".equals(role)) {
            throw ApiException.err(403, "无权限删除商品");
        }
        productService.deleteProduct(id);
        return ApiResult.ok("删除成功");
    }

    @Operation(summary = "更新商品状态", description = "更新商品上下架状态")
    @ApiResponse(responseCode = "200", description = "操作成功")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("{id}/status")
    public ApiResult<Void> updateProductStatus(
            @Parameter(hidden = true) @RequestHeader("X-Role") String role,
            @PathVariable Integer id,
            @RequestBody @Valid UpdateStatusDTO statusDTO
        ) {
        if (!"ADMIN".equals(role)) {
            throw ApiException.err(403, "无权限修改商品状态");
        }
        productService.updateProductStatus(id, statusDTO.getStatus());
        return ApiResult.ok("操作成功");
    }

    @Data
    static class ProductDTO {
        @NotNull(message = "name不可为空")
        String name;
        String subtitle;
        @NotNull(message = "categoryId不可为空")
        Integer categoryId;
        String description;
        @Min(value = 0, message = "价格不能小于0")
        BigDecimal price;
        @Min(value = 0, message = "原价不能小于0")
        BigDecimal originalPrice;
        @Min(value = 0, message = "库存不能小于0")
        Integer stock;
        List<String> imageUrls;
    }

    @Data
    static class UpdateProductDTO {
        String name;
        String subTitle;
        @Min(value = 0, message = "价格不能小于0")
        BigDecimal price;
        @Min(value = 0, message = "库存不能小于0")
        Integer stock;
        String description;
    }

    @Data
    static class UpdateStatusDTO {
        @NotNull(message = "status不可为空")
        ProductStatus status;
    }
}
