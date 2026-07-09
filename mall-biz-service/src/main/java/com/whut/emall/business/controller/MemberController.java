package com.whut.emall.business.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.business.service.MemberService;
import com.whut.emall.common.entity.ApiResult;

import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/biz/member")
public class MemberController {
    @Resource MemberService memberService;

    @GetMapping("info")
    public ApiResult getInfo(@RequestHeader("X-User-Id") Integer userId) {
        return ApiResult.ok("操作成功", memberService.getInfoById(userId));
    }
}
