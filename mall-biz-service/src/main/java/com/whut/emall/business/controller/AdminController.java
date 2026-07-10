package com.whut.emall.business.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.business.entity.enums.UserStatus;
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
    public ApiResult userCreate(@RequestBody @Valid UserCreateDTO dto) {
        if (!"CS".equals(dto.getRoleCode())) 
            throw ApiException.err(400, "前端仅能新建客服(roleCode=CS)");
        var result = adminService.userCreate(dto.getEmail(), dto.getPassword(), dto.getUsername(), dto.getPhone(), dto.getRoleCode());
        return ApiResult.ok("添加成功", result);
    }

    @GetMapping("/user/list")
    public ApiResult userList(
        @RequestParam(required = false, defaultValue = "1") Integer pageNum,
        @RequestParam(required = false, defaultValue = "10") Integer pageSize,
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String roleCode
    ) {
        return ApiResult.ok("操作成功",  adminService.userList(pageNum, pageSize, username, roleCode));
    }

    @PutMapping("/user/status")
    public ApiResult setUserStatus(@RequestHeader("X-User-Id") int selfId,@RequestBody @Valid UserStatusDTO dto) {
        if (dto.getUserId() == selfId)
            throw ApiException.err(400, "不允许对当前用户进行该操作");
        adminService.setUserStatus(dto.getUserId(), dto.getStatus());
        return ApiResult.ok("操作成功");
    }

    @Data
    static class UserCreateDTO {
        @NotBlank(message = "username不可为空") String username;
        @NotNull(message = "password不可为空") String password;
        String phone;
        @NotBlank(message = "email不可为空") String email;
        String roleCode;
    }

    @Data
    static class UserStatusDTO {
        @NotNull(message = "userId不可为空") Integer userId;
        @NotNull(message = "status不可为空") UserStatus status;
    }
}
