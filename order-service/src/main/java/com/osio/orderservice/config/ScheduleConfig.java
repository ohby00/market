package com.osio.orderservice.config;

import com.osio.orderservice.service.OrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Configuration
@EnableScheduling
@Slf4j
public class ScheduleConfig {
    private final OrderServiceImpl orderService;

    public ScheduleConfig(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @Scheduled(fixedRate = 6 * 60 * 60 * 1000)
    public void scheduled() {
        orderService.updateOrderStatus();
        log.info("Order status updated");
    }
}
