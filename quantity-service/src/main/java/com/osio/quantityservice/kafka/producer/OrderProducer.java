package com.osio.quantityservice.kafka.producer;

import com.osio.quantityservice.dto.QuantityDTO;
import com.osio.quantityservice.dto.QuantityUpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    // 1. createOrder
    public void createOrder(QuantityDTO quantityDTO) {
        String json = quantityDTO.toJson();
        log.info("create order data : {}", json);
        kafkaTemplate.send("order-create", json);
    }

    // 3-1 [Error] : 주문 삭제 롤백 진행 (결제 진행 예외)
    public void rollbackCreatedOrder(String orderId) {
        kafkaTemplate.send("order-rollback", orderId);
    }

//    // 3. 결제 진행
//    public void payment(QuantityDTO quantityDTO) {
//        String json = quantityDTO.toJson();
//        log.info("payment data : {}", json);
//        kafkaTemplate.send("payment-service", json);
//    }

    public void rollbackProduct(QuantityUpdateDTO quantityUpdateDTO) {
        String json = quantityUpdateDTO.toJson();
        log.info("rollback product data : {}", json);
        kafkaTemplate.send("rollback-product", json);
    }
}
