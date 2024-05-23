package com.osio.userservice;

import com.osio.userservice.dto.SaveDTO;
import com.osio.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

// 비동기 방식을 위한 어노테이션 (이메일 인증에서 사용)
@EnableAsync
@EnableDiscoveryClient
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }


    @Value("${admin-service.admin-email}")
    private String adminEmail;

    @Value("${admin-service.admin-password}")
    private String adminPassword;

    @Value("${user-service.user-email}")
    private String userEmail;

    @Value("${user-service.user-password}")
    private String userPassword;

    @Bean
    public CommandLineRunner initializeAdmin(UserService userService) {
        return args -> {
            // 관리자 초기화
            SaveDTO adminDto = new SaveDTO();
            adminDto.setEmail(adminEmail);
            adminDto.setPassword(adminPassword);
            adminDto.setName("관리자");
            adminDto.setPhone("000");
            adminDto.setAddress("삼천포");
            userService.saveUser(adminDto);

            // 사용자 초기화
            SaveDTO userDto = new SaveDTO();
            userDto.setEmail(userEmail);
            userDto.setPassword(userPassword);
            userDto.setName("유저");
            userDto.setPhone("000");
            userDto.setAddress("청주");
            userService.saveUser(userDto);
        };
    }
}
