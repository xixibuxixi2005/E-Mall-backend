package com.whut.emall.business.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whut.emall.common.entitiy.JwtPayload;
import com.whut.emall.common.utils.JwtUtils;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/biz/test")
public class TestController {
    @Value("${server.port}") String port;
    @Resource JwtUtils jwtUtils;
    @GetMapping
    public String test(@RequestParam(defaultValue = "") String str) {
        return "[port "+port+"] You said" + str;
    }
    @GetMapping("/token")
    public String makeToken(@RequestParam Integer id, @RequestParam String username) throws Exception{
        return jwtUtils.makeToken(new JwtPayload(id, username));
    }
    @GetMapping("/welcome")
    public String welcome(@RequestHeader(name = "X-Username", defaultValue = "游客") String username) throws Exception{
        return "你好，"+username;
    }
}
