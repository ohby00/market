package com.osio.userservice.controller;

import com.osio.userservice.dto.RefreshToken;
import com.osio.userservice.dto.TokenDTO;
import com.osio.userservice.jwt.JwtLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/token")
@RestController
@CrossOrigin
@Slf4j
public class JwtController {

    private final JwtLoginService jwtLoginService;

    public JwtController(JwtLoginService jwtLoginService) {
        this.jwtLoginService = jwtLoginService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenDTO> refreshAccessToken(@RequestBody RefreshToken refreshToken, @RequestHeader("Authorization") String accessToken) {
        // Authorization 헤더에서 토큰 추출
        String accessTokenValue = accessToken.replace("Bearer ", "");


        // JwtLogin 서비스를 사용하여 리프래쉬 토큰을 이용해 엑세스 토큰 재발급 받기
        String refreshToken2 = refreshToken.getRefreshToken();
        TokenDTO tokenDTO = jwtLoginService.refreshAccessToken(refreshToken2, accessTokenValue);
        return ResponseEntity.ok(tokenDTO);
    }

}