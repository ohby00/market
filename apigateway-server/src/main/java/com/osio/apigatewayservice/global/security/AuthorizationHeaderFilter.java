package com.osio.apigatewayservice.global.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    private final TokenValid tokenValid;

    public AuthorizationHeaderFilter(TokenValid tokenValid) {
        super(Config.class);
        this.tokenValid = tokenValid;
    }

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 1. JWT 토큰 추출
            String token = tokenValid.extractJwtTokenFromRequest(exchange);
            if (token == null) {
                return onError(exchange, "Missing JWT token in request", HttpStatus.UNAUTHORIZED);
            }

            // 2. 토큰 유효성 검사
            boolean isValid = tokenValid.validateToken(token);
            if (!isValid) {
                return onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
            }

            // 3. 사용자 ID 추출
            String userId = tokenValid.extractUserId(token);
            if (userId == null) {
                return onError(exchange, "Failed to extract user ID from JWT token - 사용자 ID 추출", HttpStatus.UNAUTHORIZED);
            }

            // 요청 헤더에 사용자 ID 추가
            exchange.getRequest().mutate().header("userId", userId);

            // 다음 필터 실행
            return chain.filter(exchange);
        };
    }

    // Error 발생 시 호출되는 메소드
    private Mono<Void> onError(ServerWebExchange exchange, String errorMsg, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
}
