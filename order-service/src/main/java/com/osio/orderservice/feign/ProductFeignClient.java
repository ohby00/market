package com.osio.orderservice.feign;

import com.osio.orderservice.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "product-service")
public interface ProductFeignClient {

    @GetMapping("/product/feign/{productId}")
    ResponseEntity<ProductDTO> getProductById(@PathVariable("productId") Long productId);
}
