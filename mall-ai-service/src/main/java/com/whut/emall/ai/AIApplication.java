package com.whut.emall.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {
    "com.whut.emall.common",
    "com.whut.emall.ai"
})
@EnableFeignClients
@EnableCaching
public class AIApplication {
    public static void main(String[] args) {
        SpringApplication.run(AIApplication.class, args);
    }
}