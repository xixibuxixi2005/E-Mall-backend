package com.whut.emall.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.SignatureAlgorithm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whut.emall.common.entity.ApiException;
import com.whut.emall.common.entity.JwtPayload;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtUtils {
    private final long EXPIRE = 2*60*60*1000;
    private final long REFRESH_EXPIRE = 24*60*60*1000;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private SecretKey secretKey;
    public JwtUtils(@Value("${jwt.secret:sssseecret__ssstttrrr}") String secret) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
        secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public String makeToken(String key, Object value, long expireIn) {
        try {
            return Jwts.builder()
                    .signWith(secretKey)
                    .expiration(new Date(System.currentTimeMillis()+expireIn))
                    .issuer("E-Mall")
                    .claim(key, objectMapper.writeValueAsString(value))
                    .compact();
        } catch (Exception e) {
            throw ApiException.err("makeToken失败: "+e.getLocalizedMessage());
        }
    }

    public String makeAccessToken(JwtPayload payload) {
        return makeToken("auth", payload, EXPIRE);
    }
    public String makeRefreshToken(JwtPayload payload) {
        return makeToken("refresh", payload, REFRESH_EXPIRE);
    }

    public <T> T parserToken(String token, String key, Class<T> cls) {
        try {
            Claims claims = (Claims)Jwts.parser().verifyWith(secretKey).build().parse(token).getPayload();
            return objectMapper.readValue(claims.get(key, String.class), cls);
        } catch (ExpiredJwtException e) {
            throw ApiException.err(401, "token已过期");
        } catch (SignatureException e) {
            throw ApiException.err(401, "token签名无效");
        } catch (MalformedJwtException e) {
            throw ApiException.err(401, "token格式错误");
        } catch (UnsupportedJwtException e) {
            throw ApiException.err(401, "token类型不支持");
        } catch (IllegalArgumentException e) {
            throw ApiException.err(401, "token为空或不合法");
        } catch (JwtException e) {
            throw ApiException.err(401, "token验证失败: " + e.getLocalizedMessage());
        } catch (Exception e) {
            throw ApiException.err("verify失败: "+e.getLocalizedMessage());
        }
    }
    public JwtPayload parserAccessToken(String token) {
        return parserToken(token, "auth", JwtPayload.class);
    }
    public JwtPayload parserRefreshToken(String token) {
        return parserToken(token, "refresh", JwtPayload.class);
    }

    public JwtPayload parseWithNoVerification(String token) {
        try {
            try {
                Claims claims = (Claims)Jwts.parser().verifyWith(secretKey).build().parse(token).getPayload();
                return objectMapper.readValue(claims.get("auth", String.class), JwtPayload.class);
            } catch (ExpiredJwtException e) {
                return objectMapper.readValue(e.getClaims().get("auth", String.class), JwtPayload.class);
            }
        } catch (Exception e) {
            throw ApiException.err("verify失败: "+e.getLocalizedMessage());
        }
    }
}
