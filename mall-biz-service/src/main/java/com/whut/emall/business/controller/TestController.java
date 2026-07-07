package com.whut.emall.business.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/biz/test")
public class TestController {
    @Value("${server.port}") String port;
    @GetMapping
    public String test(@RequestParam(defaultValue = "") String str) {
        return "[port "+port+"] You said" + str;
    }
}
