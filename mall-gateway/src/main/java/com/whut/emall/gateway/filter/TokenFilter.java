package com.whut.emall.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class TokenFilter implements GlobalFilter,Ordered{
    private final String[] publicPrefix = {
        "/api/v1/auth"
    };

    private boolean isPublic(String path) {
        for(var prefix: publicPrefix) {
            if (prefix.startsWith(prefix)) return true;
        }
        return false;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var request = exchange.getRequest();
        var response = exchange.getResponse();
        String path = request.getURI().getPath();
        
        if (isPublic(path))
            return chain.filter(exchange);
        
        String token = request.getHeaders().getFirst("Authorization");
        if (token == null || token.isEmpty()) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        request = request.mutate()
                    .header("X-User-Id", "114514")
                    .header("X-Username", "User114")
                    .build();

        exchange.mutate().request(request).build();

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
