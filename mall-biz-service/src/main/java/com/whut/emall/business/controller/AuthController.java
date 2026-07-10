package com.whut.emall.business.controller;

import java.util.Map;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.business.dto.RegisterDTO;
import com.whut.emall.business.service.AuthService;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.entity.ApiResult;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "认证接口", description = "注册、登录、刷新和退出登录")
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Resource AuthService authService;

    @Operation(summary = "用户注册", description = "提交注册信息创建新用户")
    @ApiResponse(responseCode = "200", description = "注册成功")
    @PostMapping("register")
    public String register(@RequestBody @Validated RegisterDTO dto) {
        authService.register(dto);
        return "注册成功";
    }

    @Operation(summary = "发送邮箱验证码", description = "向指定邮箱发送注册或登录验证码")
    @ApiResponse(responseCode = "200", description = "发送成功")
    @PostMapping("send-code")
    public String sendCode(@RequestBody Map<String,String> body) {
        String email = body.getOrDefault("email", "");
        if (email.isEmpty()) throw ApiException.err(400, "无效邮箱");
        authService.sendCode(email);
        return "邮箱验证码发送成功，5分钟内有效";
    }

    @Operation(summary = "用户登录", description = "使用邮箱和密码登录并返回 token 信息")
    @ApiResponse(responseCode = "200", description = "登录成功")
    @PostMapping("login")
    public ApiResult login(@RequestBody @Validated LoginDTO dto) {
        return new ApiResult("登陆成功", authService.login(dto.getEmail(), dto.getPassword()));
    }

    @Operation(summary = "刷新 token", description = "使用 refreshToken 刷新 accessToken")
    @ApiResponse(responseCode = "200", description = "刷新成功", content = @Content(schema = @Schema(implementation = ApiResult.class)))
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("refresh")
    public ApiResult refresh(
            @RequestBody Map<String,String> body,
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken
        ) {
        String refreshToken = body.getOrDefault("refreshToken", "").strip();
        if (refreshToken.isEmpty()) throw ApiException.err(400, "无效refreshToken！");
        return new ApiResult("刷新成功", authService.refresh(accessToken.substring(7), refreshToken));
    }

    @Operation(summary = "退出登录", description = "当前返回成功，后续可扩展黑名单机制")
    @ApiResponse(responseCode = "200", description = "退出成功", content = @Content(schema = @Schema(implementation = ApiResult.class)))
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("logout")
    public ApiResult logout(@Parameter(hidden = true) @RequestHeader("Authorization") String accessToken) {
        return new ApiResult("退出成功");
    }

    @Data
    static class LoginDTO {
        @NotBlank(message = "email不能为空") String email;
        @NotNull(message = "password不能为空") String password;
    }
}
