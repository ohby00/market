package com.osio.orderservice.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osio.orderservice.dto.QuantityDTO;
import com.osio.orderservice.kafka.producer.QuantityProducer;
import com.osio.orderservice.service.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RollBackConsumer {
    private final OrderServiceImpl orderServiceImpl;


    // 2-1 [Error] : 주문 삭제 진행 완료
    @KafkaListener(topics = "order-rollback", groupId = "group-01")
    public void rollbackOrder(Long orderId){
        log.error("======= [Rollback] order-rollback, orderId: {} ========", orderId);
        orderServiceImpl.deleteOrder(orderId);
    }
}
