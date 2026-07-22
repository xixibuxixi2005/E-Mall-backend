package com.whut.emall.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import com.whut.emall.common.entity.ApiException;

@Component
public class BackendFilter implements GlobalFilter,Ordered{
    private final String[] backendPrefix = {
        "/api/biz/order/list",
        "/api/chat/cs/",
        "/api/chat/sentiment/",
        "/api/ai/knowledge",
        "/static/doc/",
    };
    private final String[] nonBackendPrefix = {
    };
    private boolean isBackendPath(String path) {
        for(var prefix: nonBackendPrefix) {
            if (path.startsWith(prefix)) return false;
        }
        for(var prefix: backendPrefix) {
            if (path.startsWith(prefix)) return true;
        }
        return false;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var request = exchange.getRequest();

        String path = request.getURI().getPath();
        String role = request.getHeaders().getFirst("X-Role");

        if (isBackendPath(path))
            if (!("ADMIN".equals(role) || "CS".equals(role)))
                throw ApiException.err(HttpStatus.UNAUTHORIZED.value(), "用户权限不足：需要管理员/客服权限");

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -51;
    }
}
