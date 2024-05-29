//package com.osio.userservice.config;
//
//import lombok.Data;
//import lombok.Getter;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
//import org.springframework.stereotype.Component;
//
//@Data
//@RefreshScope       // Config.yml 파일 변경 시 변경된 내용을 actuator 통해 변경값을 갱신
//@Component
//public class LocalConfig {
//    // {application-profiles}.yml 에 정의한 내용을 해당 변수에 넣어줌
//    @Value("${spring.datasource.username}")
//    private String username;
//
//    @Value("${spring.datasource.password}")
//    private String password;
//
//    @Override
//    public String toString() {
//        return "LocalConfig{" +
//                "username='" + username + '\'' +
//                ", password='" + password + '\'' +
//                '}';
//    }
//}