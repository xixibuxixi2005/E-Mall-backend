package com.whut.emall.business.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.business.entity.Member;
import com.whut.emall.business.service.MemberService;

import jakarta.annotation.Resource;
import jakarta.websocket.server.PathParam;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/biz/member")
public class MemberController {
    @Resource MemberService memberService;

    @GetMapping("/{id}")
    public Member getById(@PathParam("id") Long id) {
        return memberService.getMemberById(id);
    }
}
