package com.whut.emall.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

@Configuration
public class MinIOConfig {
    @Value("${minio.endpoint}")
    String endpoint;
    @Value("${minio.access_key}")
    String access_key;
    @Value("${minio.secret_key}")
    String secret_key;
    @Bean
    public MinioClient minioClient() {
        return new MinioClient.Builder()
            .endpoint(endpoint)
            .credentials(access_key, secret_key)
            .build();
    }
}
