package com.whut.emall.business.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.business.service.MemberService;
import com.whut.emall.common.entity.ApiResult;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "会员接口", description = "会员个人资料和密码管理")
@RestController
@RequestMapping("/biz/member")
public class MemberController {
    @Resource MemberService memberService;

    @Operation(summary = "获取会员信息", description = "根据网关注入的 X-User-Id 获取当前会员信息")
    @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = ApiResult.class)))
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("info")
    public ApiResult getInfo(@Parameter(hidden = true) @RequestHeader("X-User-Id") Integer userId) {
        return ApiResult.ok("操作成功", memberService.getInfoById(userId));
    }
    
    @Operation(summary = "修改会员信息", description = "更新当前会员的邮箱、手机号和头像")
    @ApiResponse(responseCode = "200", description = "修改成功", content = @Content(schema = @Schema(implementation = ApiResult.class)))
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("info")
    public ApiResult setInfo(@Parameter(hidden = true) @RequestHeader("X-User-Id") Integer userId, @RequestBody @Valid UserInfoDTO dto) {
        memberService.setInfoById(userId, dto.getEmail(), dto.getPhone(), dto.getAvatar());
        return ApiResult.ok("修改成功");
    }

    @Operation(summary = "修改密码", description = "使用旧密码修改当前会员密码")
    @ApiResponse(responseCode = "200", description = "修改成功", content = @Content(schema = @Schema(implementation = ApiResult.class)))
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("password")
    public ApiResult setPassword(@Parameter(hidden = true) @RequestHeader("X-User-Id") Integer userId, @RequestBody @Valid PasswordDTO dto) {
        memberService.setPassword(userId, dto.getOldPassword(), dto.getNewPassword());
        return ApiResult.ok("密码修改成功");
    }

    @Data
    static class UserInfoDTO {
        String email;
        String phone;
        String avatar;
    }
    
    @Data
    static class PasswordDTO {
        @NotNull(message = "oldPassword不可为空")
        String oldPassword;
        @NotNull(message = "newPassword不可为空")
        @Size(min = 6, max = 20, message = "密码长度需为6-20位")
        String newPassword;
    }
}
