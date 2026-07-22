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
public class AdminFilter implements GlobalFilter,Ordered{
    private final String[] adminPrefix = {
        "/api/admin",
        "/api/ai/predict",
    };
    private final String[] nonAdminPrefix = {
    };
    private boolean isAdminPath(String path) {
        for(var prefix: nonAdminPrefix) {
            if (path.startsWith(prefix)) return false;
        }
        for(var prefix: adminPrefix) {
            if (path.startsWith(prefix)) return true;
        }
        return false;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var request = exchange.getRequest();

        String path = request.getURI().getPath();
        String role = request.getHeaders().getFirst("X-Role");

        if (isAdminPath(path) && !("ADMIN".equals(role))) {
            throw ApiException.err(HttpStatus.UNAUTHORIZED.value(), "用户权限不足：需要管理员权限");
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -50;
    }
}
