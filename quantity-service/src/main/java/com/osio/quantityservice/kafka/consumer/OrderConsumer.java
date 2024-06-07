package com.osio.quantityservice.kafka.consumer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osio.quantityservice.dto.QuantityDTO;
import com.osio.quantityservice.service.QuantityServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class  OrderConsumer {

    private final QuantityServiceImpl quantityServiceImpl;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 0. 재고 확인 및 감소
    @KafkaListener(topics = "check-quantity-Mysql", groupId = "group-01")
    public void checkQuantityMysql(String kafkaMessage) throws JsonProcessingException {
        log.info("check-quantity-Mysql: {}", kafkaMessage);

        QuantityDTO quantityDTO = objectMapper.readValue(kafkaMessage, QuantityDTO.class);
        quantityServiceImpl.decreaseQuantityMysql(quantityDTO);
    }

    // 0. 재고 확인 및 감소
    @KafkaListener(topics = "check-quantity-Redis", groupId = "group-01")
    public void checkQuantityRedis(String kafkaMessage) throws JsonProcessingException {
        log.info("check-quantity-Redis: {}", kafkaMessage);

        QuantityDTO quantityDTO = objectMapper.readValue(kafkaMessage, QuantityDTO.class);
       quantityServiceImpl.decreaseQuantityRedis(quantityDTO);
    }
}
