package com.osio.orderservice.feign;

import com.osio.orderservice.config.FeignConfig;
import com.osio.orderservice.dto.QuantityDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "quantity-service", configuration = FeignConfig.class)
public interface QuantityFeignClient {

    @PostMapping("/quantity/feign")
    String decreaseQuantity(@RequestBody QuantityDTO quantityDTO);
}
