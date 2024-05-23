//package com.osio.quantityservice.config;
//
//import com.osio.quantityservice.service.QuantityServiceImpl;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//@Configuration
//@EnableScheduling
//@Slf4j
//public class ScheduleConfig {
//    private final QuantityServiceImpl quantityService;
//
//    public ScheduleConfig(QuantityServiceImpl quantityService) {
//        this.quantityService = quantityService;
//    }
//
//    @Scheduled(fixedRate = 6 * 60 * 60 * 1000)
//    public void scheduled() {
//        quantityService.updateQuantity();
//        log.info("Quantity updated");
//    }
//}
