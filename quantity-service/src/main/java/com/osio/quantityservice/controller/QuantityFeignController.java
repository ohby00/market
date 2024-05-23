package com.osio.quantityservice.controller;

import com.osio.quantityservice.dto.QuantityDTO;
import com.osio.quantityservice.service.QuantityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/quantity/feign")
@AllArgsConstructor
public class QuantityFeignController {
    private final QuantityService quantityService;

    @GetMapping("/{productId}")
    public Long getProductById(@PathVariable("productId") Long productId) {
        return quantityService.getProductQuantity(productId);
    }
}