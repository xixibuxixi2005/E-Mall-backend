package com.whut.emall.business.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.business.service.AdminService;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.entity.ApiResult;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource AdminService adminService;

    @PostMapping("/user/create")
    public ApiResult userCreate(@RequestBody @Valid userCreateDTO dto) {
        if (!"CS".equals(dto.getRoleCode())) 
            throw ApiException.err(400, "前端仅能新建客服(roleCode=CS)");
        var result = adminService.userCreate(dto.getEmail(), dto.getPassword(), dto.getUsername(), dto.getPhone(), dto.getRoleCode());
        return ApiResult.ok("添加成功", result);
    }

    @Data
    static class userCreateDTO {
        @NotBlank(message = "username不可为空") String username;
        @NotNull(message = "password不可为空") String password;
        String phone;
        @NotBlank(message = "email不可为空") String email;
        String roleCode;
    }
}
