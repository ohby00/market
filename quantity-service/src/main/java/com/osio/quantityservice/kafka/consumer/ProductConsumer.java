package com.osio.quantityservice.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osio.quantityservice.dto.QuantityDTO;
import com.osio.quantityservice.dto.QuantityUpdateDTO;
import com.osio.quantityservice.kafka.producer.OrderProducer;
import com.osio.quantityservice.service.QuantityServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class  ProductConsumer {
    private final QuantityServiceImpl quantityService;
    private final OrderProducer orderProducer;

    // 물건 업데이트
    @KafkaListener(topics = "quantity-update", groupId = "group-01")
    public void consume(String kafkaMessage) throws JsonProcessingException {
        log.info("Received kafka message: {}", kafkaMessage);

        ObjectMapper objectMapper = new ObjectMapper();
        QuantityUpdateDTO quantityUpdateDTO = objectMapper.readValue(kafkaMessage, QuantityUpdateDTO.class);

        try {
            // 물건 수량 업데이트, 추가 할 때 에러 생기면 둘다 삭제
            quantityService.updateQuantity(quantityUpdateDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
            orderProducer.rollbackProduct(quantityUpdateDTO);
        }
    }



}
