package com.whut.emall.business.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.whut.emall.business.service.UploadService;
import com.whut.emall.common.entity.ApiResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/biz/upload")
@Tag(name = "上传接口", description = "提供图片上传并返回url接口")
public class UploadController {
    @Resource UploadService uploadService;

    @Operation(summary = "上传图片", description = "将文件上传至对象存储服务器并返回url列表")
    @ApiResponse(responseCode = "200", description = "上传成功")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("images")
    public ApiResult<List<String>> postMethodName(@RequestParam MultipartFile[] images) {
        return ApiResult.ok("上传成功", uploadService.uploadImages(images));
    }
    
}
