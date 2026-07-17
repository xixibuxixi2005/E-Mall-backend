package com.whut.emall.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.entity.JwtPayload;
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
        "/api/biz/v3/api-docs",
        "/api/ai/v3/api-docs",
    };
    private final String[] nonPublicPrefix = {
        "/api/biz/test/welcome",
        "/api/auth/refresh",
    };
    private boolean isPublic(String path) {
        for(var prefix: nonPublicPrefix) {
            if (path.startsWith(prefix)) return false;
        }
        for(var prefix: publicPrefix) {
            if (path.startsWith(prefix)) return true;
        }
        return false;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        if (isPublic(path))
            return chain.filter(exchange);
        
        String token = request.getHeaders().getFirst("Authorization");
        if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
            throw ApiException.err(HttpStatus.UNAUTHORIZED.value(), "Authorization token格式错误");
        }

        if (path.equals("/api/auth/refresh"))
            return chain.filter(exchange);

        JwtPayload payload;
        try {
            payload = jwtUtils.parserAccessToken(token.substring(7));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            throw ApiException.err(HttpStatus.UNAUTHORIZED.value(), "Authorization token验证失败");
        }

        request = request.mutate()
                    .header("X-User-Id", ""+payload.getUserId())
                    .header("X-Email", payload.getEmail())
                    .header("X-Role", ""+payload.getRoleCode())
                    .build();

        exchange = exchange.mutate().request(request).build();

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
