package com.whut.emall.ai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.ai.service.PredictService;
import com.whut.emall.ai.vo.UserProfileVO;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.entity.ApiResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;

@RestController
@RequestMapping("/profile")
@Tag(name = "会员画像", description = "会员画像标签")
@SecurityRequirement(name = "Authorization")
public class ProfileController {
    @Resource PredictService predictService;

    @Operation(summary = "获取会员画像", description = "获取当前会员的画像标签")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("labels")
    public ApiResult<UserProfileVO> getUserProfile(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Integer uId,
            @Parameter(hidden = true) @RequestHeader("X-Role") String role,
            @RequestParam Integer userId
        ) throws Exception{
        if (!"ADMIN".equals(role) && !uId.equals(userId)) throw ApiException.err(403, "无权限访问");
        return ApiResult.ok("获取成功", predictService.getUserProfile(userId));
    }
}
