package com.whut.emall.business.config;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.whut.emall.common.entity.ApiResult;

@RestControllerAdvice
public class ApiResponseHandler implements ResponseBodyAdvice<ApiResult<?>>{
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public ApiResult<?> beforeBodyWrite(ApiResult<?> body, MethodParameter returnType, MediaType seselectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        
        response.setStatusCode(HttpStatusCode.valueOf(body.getCode()));
        return body;
    }
}
