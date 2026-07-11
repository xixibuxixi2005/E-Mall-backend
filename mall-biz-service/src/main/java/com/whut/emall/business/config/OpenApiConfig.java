package com.whut.emall.business.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "E-Mall 业务微服务 API",
        version = "v1",
        description = "业务微服务接口文档。",
        contact = @Contact(name = "E-Mall")
    )
)
@SecurityScheme(
    name = "Authorization",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi apis() {
        return GroupedOpenApi.builder()
                .group("biz")
                .packagesToScan("com.whut.emall.business.controller")
                .build();
    }
}
