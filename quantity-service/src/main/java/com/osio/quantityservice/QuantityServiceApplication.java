package com.osio.quantityservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableDiscoveryClient
@SpringBootApplication
public class QuantityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuantityServiceApplication.class, args);
    }

}
