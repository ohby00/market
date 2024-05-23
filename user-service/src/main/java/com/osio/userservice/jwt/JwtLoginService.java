package com.osio.userservice.jwt;

import com.osio.userservice.dto.LoginDTO;
import com.osio.userservice.dto.TokenDTO;
import com.osio.userservice.entity.User;
import com.osio.userservice.repository.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtLoginService {
    private final UserJpaRepository userRepository;
    private final JwtService jwtService;

    public JwtLoginService(UserJpaRepository userRepository,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;

    }

    public TokenDTO login(LoginDTO request) {
        log.info("로그인 -> login_request: {}", request);
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtService.generateAccessToken(user.getId());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        return TokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenDTO refreshAccessToken(String refreshToken, String accessTokenValue) {
        String userId = jwtService.extractUserId(refreshToken);
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (jwtService.validateToken(refreshToken, user.getId())) {
            String newAccessToken = jwtService.generateAccessToken(user.getId());
            return TokenDTO.builder()
                    .accessToken(newAccessToken)
                    .build();
        } else {
            throw new RuntimeException("Invalid refresh token");
        }
    }
}
