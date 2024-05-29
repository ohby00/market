//package com.osio.userservice.controller;
//
//import com.osio.userservice.config.LocalConfig;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping(value = "/service")
//public class LocalController {
//
//    // Configuration 주입
//    private final LocalConfig localConfig;
//
//    // 생성자 주입
//    public LocalController(LocalConfig localConfig) {
//        this.localConfig = localConfig;
//    }
//
//    // /service/local 엔드포인트 호출 시 "config-local.yml 정의한 내용 불러옴"
//    @GetMapping("/local")
//    public String loadLocalConfig() {
//        return localConfig.toString();
//    }
//}