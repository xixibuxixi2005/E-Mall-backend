package com.whut.emall.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.entity.Member;

import jakarta.websocket.server.PathParam;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/member")
public class MemberController {
    @GetMapping("/{id}")
    public Member getById(@PathParam("id") Integer id) {
        Member member = new Member();
        member.setId(id);
        return member;
    }
}
