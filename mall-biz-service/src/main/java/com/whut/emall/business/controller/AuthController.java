package com.whut.emall.business.controller;

import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.business.dto.LoginDTO;
import com.whut.emall.business.dto.RegisterDTO;
import com.whut.emall.business.service.AuthService;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.entity.ApiResult;

import jakarta.annotation.Resource;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/auth")
public class AuthController {
    @Resource AuthService authService;

    @PostMapping("register")
    public String register(@RequestBody RegisterDTO dto) {
        authService.register(dto);
        return "注册成功";
    }
    @PostMapping("send-code")
    public String sendCode(@RequestBody Map<String,String> body) {
        String email = body.getOrDefault("email", "");
        if (email.isEmpty())
            throw ApiException.err(400, "无效邮箱");
        authService.sendCode(email);
        return "邮箱验证码发送成功，5分钟内有效";
    }
    

    @PostMapping("login")
    public ApiResult login(@RequestBody LoginDTO dto) {
        return new ApiResult("登陆成功", authService.login(dto));
    }
    
    @PostMapping("refresh")
    public ApiResult refresh(
            @RequestBody Map<String,String> body,
            @RequestHeader("Authorization") String accessToken
        ) {
        String refreshToken = body.getOrDefault("refreshToken", "").strip();
        if (refreshToken.isEmpty())
            throw ApiException.err(400, "无效refreshToken！");
        return new ApiResult("刷新成功", authService.refresh(accessToken.substring(7), refreshToken));
    }
    
    @PostMapping("logout")
    public ApiResult logout(@RequestHeader("Authorization") String accessToken) {
        // TODO 添加黑名单功能
        return new ApiResult("退出成功");
    }
}
