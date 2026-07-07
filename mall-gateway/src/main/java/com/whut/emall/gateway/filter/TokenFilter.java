package com.whut.emall.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.whut.emall.common.entitiy.JwtPayload;
import com.whut.emall.common.utils.JwtUtils;

import jakarta.annotation.Resource;
import reactor.core.publisher.Mono;

@Component
public class TokenFilter implements GlobalFilter,Ordered{
    @Resource JwtUtils jwtUtils;
    Logger logger = LoggerFactory.getLogger(getClass());

    private final String[] publicPrefix = {
        "/api/biz/test",
        "/api/auth",
    };

    private boolean isPublic(String path) {
        for(var prefix: publicPrefix) {
            if (path.startsWith(prefix)) return true;
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
        if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        JwtPayload payload;
        try {
            payload = jwtUtils.verify(token.substring(7));
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        request = request.mutate()
                    .header("X-User-Id", ""+payload.getUserId())
                    .header("X-Username", payload.getUsername())
                    .build();

        exchange.mutate().request(request).build();

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
