package com.whut.emall.business.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.business.service.ProductService;
import com.whut.emall.common.entity.ApiResult;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/biz/product")
public class ProductController {
    @Resource ProductService productService;

    @GetMapping("{id}")
    public ApiResult getProductDetail(@PathVariable Integer id) {
        return ApiResult.ok("操作成功", productService.getProductById(id));
    }
}
