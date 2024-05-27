package com.osio.userservice.controller;

import com.osio.userservice.dto.*;
import com.osio.userservice.entity.User;
import com.osio.userservice.jwt.JwtLoginService;
import com.osio.userservice.repository.UserJpaRepository;
import com.osio.userservice.service.EmailService;
import com.osio.userservice.service.RedisService;
import com.osio.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@RequestMapping("/user")
@RestController
@CrossOrigin()
@Slf4j
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final UserJpaRepository userJpaRepository;
    private final JwtLoginService jwtLoginService;
    private final RedisService redisService;

    public UserController(UserService userService,
                          EmailService emailService,
                          UserJpaRepository userJpaRepository,
                          JwtLoginService jwtLoginService,
                          RedisService redisService) {
        this.userService = userService;
        this.emailService = emailService;
        this.userJpaRepository = userJpaRepository;
        this.jwtLoginService = jwtLoginService;
        this.redisService = redisService;
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> jwtLogin(@RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(jwtLoginService.login(loginDTO));
    }

    // 마이페이지
    @GetMapping("/myPage")
    public ResponseEntity<User> jwtMyPage(@RequestHeader("userId") Long userId) {
        log.info("마이페이지 모듈 실행");
        User user = userJpaRepository.findById(userId).orElse(null);
        return ResponseEntity.ok(user);
    }

    // 모든 유저 조회
    @GetMapping("/list")
    public ResponseEntity<Object> selectUserList() {
        List<User> userEntityList = userService.getAllUser();
        return new ResponseEntity<>(userEntityList, HttpStatus.OK);
    }

    // 회원가입
    @PostMapping("/save")
    public ResponseEntity<Object> userSave(@RequestBody SaveDTO saveDTO) {
        User result = userService.saveUser(saveDTO);
        log.info("회원가입");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Email 인증 전송
    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailDTO emailDTO) {
        if (emailDTO == null ) {
            return new ResponseEntity<>("이메일 주소를 입력하세요.", HttpStatus.BAD_REQUEST);
        }
        try {
            log.info(emailDTO.getEmail());
            String randomCode = generateCode();
            emailService.send(emailDTO.getEmail(), "인증 코드 발송", "인증 코드: " + randomCode);
            redisService.setDataExpire(emailDTO.getEmail(), randomCode, 30);
            log.info("이메일 전송 완료");
            return new ResponseEntity<>("이메일 전송 성공", HttpStatus.OK);
        } catch (Exception e) {
            log.info("e = {} ", e.getMessage());
            return new ResponseEntity<>("이메일 전송 실패", HttpStatus.OK);
        }
    }

    // 인증 번호 랜덤 생성
    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 랜덤 숫자 생성 (100000 ~ 999999)
        return String.valueOf(code);
    }

    // 이메일 인증
    @PostMapping("/checkEmail")
    public ResponseEntity<String> checkEmail(@RequestBody CheckEmailDTO checkEmailDTO) {
        // 이메일 인증 로직을 수행 및 인증 결과에 따른 응답 반환

        String frontCode = checkEmailDTO.getVerificationCode();
        log.info("frontCode = {} ",frontCode);

        String email = checkEmailDTO.getEmail();
        log.info("email = {} ",email);

        String generateCode = redisService.getData(email);
        log.info("generateCode = {} ",generateCode);

        if (validCode(frontCode, generateCode)) {
            log.info(frontCode);
            return new ResponseEntity<>("이메일 인증 성공", HttpStatus.OK);
        } else {
            log.info(frontCode);
            return new ResponseEntity<>("인증 코드가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // 토큰 유효성 검사
    private boolean validCode(String frontCode, String generateCode) {
        return frontCode.equals(generateCode);
    }

    // 해당 user 찾기
    @PostMapping("/find/{id}")
    public Optional<User> findId(@PathVariable("id") Long id) {
        return userService.findByUserId(id);
    }

    // 해당 user 탈퇴
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUserByUserId(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
