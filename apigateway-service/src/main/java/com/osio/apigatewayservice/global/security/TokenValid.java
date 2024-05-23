package com.osio.apigatewayservice.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.Date;

@Slf4j
@Component
public class TokenValid {
    private static final String SECRET_KEY = "ZaOhvbAdBvomPAZmhFgcDBaaDNdDBQgGBpv72m85lxj0CNczYpahT6xa0RjcsOnp";

    // 1. JWT 토큰 추출
    public String extractJwtTokenFromRequest(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String authorizationHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        String token = authorizationHeader.replace("Bearer","");

        log.info("token: {}", token);
        return token;
    }

    // 2. 토큰 유효성 검사
    public boolean validateToken(String token) {
        final String userId = extractUserId(token);
        return (userId != null && !isTokenExpired(token));
    }

    // 3. 사용자 ID 추출
    public String extractUserId(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token).getBody();
            return claims.getSubject();
        } catch (Exception e) {
            log.error("Failed to extract user ID from - token extractUserId", e);
            return null;
        }
    }

    // 토큰 유효성 검사 시, 유효기간 확인
    private boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token).getBody();
            Date expirationDate = claims.getExpiration();
            return expirationDate.before(new Date());
        } catch (Exception e) {
            log.error("Failed to extract expiration date from token", e);
            return true;
        }
    }
}
