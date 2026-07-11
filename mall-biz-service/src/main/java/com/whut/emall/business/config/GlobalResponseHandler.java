package com.whut.emall.business.config;

import org.springframework.http.HttpStatus;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whut.emall.common.entity.ApiResult;

@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object>{
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (returnType.hasMethodAnnotation(EMallResponse.class) || returnType.getDeclaringClass().isAnnotationPresent(EMallResponse.class))
            return true;
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ApiResult) {
            ApiResult<?> result = (ApiResult<?>) body;
            response.setStatusCode(HttpStatusCode.valueOf(result.getCode()));
            return body;
        }

        if (body instanceof String) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            try {
                return objectMapper.writeValueAsString(ApiResult.ok((String) body));
            } catch (JsonProcessingException e) {
                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                return "{\"code\":500,\"msg\":\"响应序列化失败\",\"data\":null)";
            }
        }

        return ApiResult.ok("", body);
    }
}
