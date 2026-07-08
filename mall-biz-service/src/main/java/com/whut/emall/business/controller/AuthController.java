package com.whut.emall.business.controller;

import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.business.dto.LoginDTO;
import com.whut.emall.business.service.AuthService;
import com.whut.emall.common.entity.ApiResult;

import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/auth")
public class AuthController {
    @Resource AuthService authService;
    @PostMapping("login")
    public ApiResult login(@RequestBody LoginDTO dto) {
        return new ApiResult("登陆成功", authService.login(dto));
    }
    
}
