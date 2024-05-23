package com.osio.orderservice.feign;

import com.osio.orderservice.dto.ProductDTO;
import com.osio.orderservice.dto.QuantityDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "quantity-service")
public interface QuantityFeignClient {

    @GetMapping("/quantity/feign/{productId}")
    ResponseEntity<Long> getProductQuantity(@PathVariable("productId") Long productId);
}
