package com.whut.emall.business.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.whut.emall.business.entity.Member;
import com.whut.emall.business.mapper.MemberMapper;

import java.util.List;

@RestController
@RequestMapping("/biz/test")
public class TestController {
    @Value("${server.port}") String port;
    @Resource MemberMapper memberMapper;
    @GetMapping
    public String test(@RequestParam(defaultValue = "") String str) {
        return "[port "+port+"] You said" + str;
    }
    @GetMapping("/welcome")
    public String welcome(@RequestHeader(name = "X-Username", defaultValue = "游客") String username) throws Exception{
        return "你好，"+username;
    }
    @GetMapping("/all_members")
    public List<Member> allMembers(){
        return memberMapper.selectList(null);
    }
}
