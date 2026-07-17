package com.whut.emall.gateway.exception;

import java.nio.charset.StandardCharsets;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.entity.ApiResult;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GatewayExceptionHandler implements ErrorWebExceptionHandler, Ordered {

    @Resource
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(GatewayExceptionHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        ApiResult<?> result;
        HttpStatusCode statusCode = HttpStatus.INTERNAL_SERVER_ERROR;

        if (ex instanceof ApiException apiException) {
            result = apiException.toResult();
            statusCode = HttpStatus.valueOf(result.getCode());
        } else if (ex instanceof ResponseStatusException responseStatusException) {
            statusCode = responseStatusException.getStatusCode();
            result = new ApiResult<>(statusCode.value(), responseStatusException.getReason() == null ? statusCode.toString() : responseStatusException.getReason(), null);
        } else {
            logger.error("Gateway exception", ex);
            result = new ApiResult<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage() == null ? "Gateway error" : ex.getMessage(), null);
        }

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(statusCode);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(result);
        } catch (Exception e) {
            logger.error("Failed to serialize ApiResult", e);
            bytes = ("{\"code\":500,\"message\":\"Response serialization failed\",\"data\":null}").getBytes(StandardCharsets.UTF_8);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
