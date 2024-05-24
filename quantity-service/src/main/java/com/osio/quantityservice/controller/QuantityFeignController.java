package com.osio.quantityservice.controller;

import com.osio.quantityservice.dto.QuantityDTO;
import com.osio.quantityservice.service.QuantityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/quantity")
@AllArgsConstructor
public class QuantityFeignController {
    private final QuantityService quantityService;

    @PostMapping("/feign")
    public String decreaseQuantity(@RequestBody QuantityDTO quantityDTO) {
        return quantityService.decreaseQuantity(quantityDTO);
    }
}