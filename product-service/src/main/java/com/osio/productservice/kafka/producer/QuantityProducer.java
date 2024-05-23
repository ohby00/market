package com.osio.productservice.kafka.producer;

import com.osio.productservice.dto.QuantityUpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuantityProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(QuantityUpdateDTO quantityUpdateDTO) {
        String json = quantityUpdateDTO.toJson();
        log.info("Sending quantity update to {}", json);
        kafkaTemplate.send("quantity-update", json);
    }
}
