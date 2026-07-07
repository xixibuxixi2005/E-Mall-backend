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
import com.whut.emall.common.entitiy.JwtPayload;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtUtils {
    private final long EXPIRE = 2*60*60*1000;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private SecretKey secretKey;
    public JwtUtils(@Value("${jwt.secret:sssseecret__ssstttrrr}") String secret) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
        secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public String makeToken(JwtPayload payload) throws Exception{
        return Jwts.builder()
                .signWith(secretKey)
                .expiration(new Date(System.currentTimeMillis()+EXPIRE))
                .subject(payload.getUsername())
                .claim("auth", objectMapper.writeValueAsString(payload))
                .compact();
    }

    public JwtPayload verify(String token) throws Exception{
        Claims claims = (Claims)Jwts.parser().verifyWith(secretKey).build().parse(token).getPayload();
        return objectMapper.readValue(claims.get("auth", String.class), JwtPayload.class);
    }
}
