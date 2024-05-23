package com.osio.productservice.controller;

import com.osio.productservice.dto.ProductDTO;
import com.osio.productservice.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/product/feign")
@AllArgsConstructor
public class ProductFeignController {
    private final ProductService productService;

    @GetMapping("/{productId}")
    public ProductDTO getProductById(@PathVariable("productId") Long productId) {
        return productService.getProduct(productId);
    }
}