package com.whut.emall.business.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.business.config.EMallResponse;
import com.whut.emall.business.entity.enums.UserStatus;
import com.whut.emall.business.service.AdminService;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.entity.ApiResult;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "管理接口", description = "用户管理相关接口")
@RestController
@RequestMapping("/admin")
@EMallResponse
public class AdminController {
    @Resource AdminService adminService;

    @Operation(summary = "创建客服用户", description = "仅允许创建 roleCode=CS 的客服账号")
    @ApiResponse(responseCode = "200", description = "创建成功")
    @PostMapping("/user/create")
    public ApiResult userCreate(@RequestBody @Valid UserCreateDTO dto) {
        if (!"CS".equals(dto.getRoleCode())) 
            throw ApiException.err(400, "前端仅能新建客服(roleCode=CS)");
        var result = adminService.userCreate(dto.getEmail(), dto.getPassword(), dto.getUsername(), dto.getPhone(), dto.getRoleCode());
        return ApiResult.ok("添加成功", result);
    }

    @Operation(summary = "分页查询用户", description = "按用户名和角色筛选用户列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping("/user/list")
    public ApiResult userList(
        @RequestParam(required = false, defaultValue = "1") Integer pageNum,
        @RequestParam(required = false, defaultValue = "10") Integer pageSize,
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String roleCode
    ) {
        return ApiResult.ok("操作成功",  adminService.userList(pageNum, pageSize, username, roleCode));
    }

    @Operation(summary = "更新用户状态", description = "管理员可修改指定用户状态，不能修改自己")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/user/status")
    public ApiResult setUserStatus(@Parameter(hidden = true) @RequestHeader("X-User-Id") int selfId,@RequestBody @Valid UserStatusDTO dto) {
        if (dto.getUserId() == selfId)
            throw ApiException.err(400, "不允许对当前用户进行该操作");
        adminService.setUserStatus(dto.getUserId(), dto.getStatus());
        return ApiResult.ok("更新成功");
    }

    @Data
    static class UserCreateDTO {
        @NotBlank(message = "username不可为空") String username;
        @NotNull(message = "password不可为空")
        @Size(min = 6, max = 20, message = "密码长度需为6-20位")
        String password;
        String phone;
        @NotBlank(message = "email不可为空")
        @Email(message = "邮箱格式错误") String email;
        String roleCode;
    }

    @Data
    static class UserStatusDTO {
        @NotNull(message = "userId不可为空") Integer userId;
        @NotNull(message = "status不可为空") UserStatus status;
    }
}
