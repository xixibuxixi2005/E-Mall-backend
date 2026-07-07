package com.whut.emall.business.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.business.entity.Member;

import jakarta.websocket.server.PathParam;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/biz/member")
public class MemberController {
    @GetMapping("/{id}")
    public Member getById(@PathParam("id") Integer id) {
        Member member = new Member();
        member.setId(id);
        return member;
    }
}
