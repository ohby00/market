package com.osio.quantityservice.service;

import com.osio.quantityservice.dto.QuantityDTO;
import com.osio.quantityservice.dto.QuantityUpdateDTO;
import org.springframework.http.ResponseEntity;

public interface QuantityService {

    ResponseEntity<String> decreaseQuantityRedis(QuantityDTO quantityDTO);

    ResponseEntity<String> decreaseQuantityMysql(QuantityDTO quantityDTO);

    // 재고 DB 저장
    void updateQuantity(QuantityUpdateDTO quantityUpdateDTO);

//    Long getProductQuantity(Long productId);
}
