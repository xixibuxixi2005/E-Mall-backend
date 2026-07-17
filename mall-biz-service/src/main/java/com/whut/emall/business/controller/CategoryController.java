package com.whut.emall.business.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.business.service.CategoryService;
import com.whut.emall.common.entity.ApiResult;
import com.whut.emall.common.entity.Category;
import com.whut.emall.common.entity.ApiException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "商品分类接口", description = "商品分类查询与管理")
@RestController
@RequestMapping("/biz/category")
public class CategoryController {
    @Resource CategoryService categoryService;

    @Operation(summary = "商品分类列表", description = "获取所有商品分类信息")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @SecurityRequirement(name = "Authorization")
    @GetMapping
    public ApiResult<List<Category>> getCategories() {
        return ApiResult.ok("查询成功", categoryService.getCategories());
    }

    @Operation(summary = "创建分类", description = "创建新的商品分类，支持多级层级（最多3级）")
    @ApiResponse(responseCode = "200", description = "创建成功")
    @SecurityRequirement(name = "Authorization")
    @PostMapping
    public ApiResult<Category> createCategory(
            @Parameter(hidden = true) @RequestHeader("X-Role") String role,
            @RequestBody @Valid CategoryDTO categoryDTO
        ) {
        if (!"ADMIN".equals(role))
            throw ApiException.err(403, "无权限创建分类");
        Category category = categoryService.createCategory(categoryDTO.getName(), categoryDTO.getParentId(), categoryDTO.getSortOrder());
        return ApiResult.ok("创建成功", category);
    }

    @Operation(summary = "更新分类", description = "更新分类名称与排序字段")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("{id}")
    public ApiResult<Void> updateCategory(
            @Parameter(hidden = true) @RequestHeader("X-Role") String role,
            @PathVariable Integer id,
            @RequestBody UpdateCategoryDTO categoryDTO
        ) {
        if (!"ADMIN".equals(role))
            throw ApiException.err(403, "无权限更新分类");
        categoryService.updateCategory(id, categoryDTO.getName(), categoryDTO.getSortOrder());
        return ApiResult.ok("更新成功");
    }

    @Operation(summary = "删除分类", description = "删除指定分类，如果有子分类则不允许删除")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("{id}")
    public ApiResult<Void> deleteCategory(
            @Parameter(hidden = true) @RequestHeader("X-Role") String role,
            @PathVariable Integer id
        ) {
        if (!"ADMIN".equals(role))
            throw ApiException.err(403, "无权限删除分类");
        categoryService.deleteCategory(id);
        return ApiResult.ok("删除成功");
    }

    @Data
    static class CategoryDTO {
        @NotBlank(message = "name字段不能为空")
        String name;
        Integer parentId;
        Integer sortOrder;
    }

    @Data
    static class UpdateCategoryDTO {
        String name;
        Integer sortOrder;
    }
}
