package com.osio.orderservice.kafka.producer;


import com.osio.orderservice.dto.QuantityDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuantityProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    // 0. 재고 확인 및 감소
    public void checkQuantityMysql(QuantityDTO quantityDTO){
        String json = quantityDTO.toJson();
        log.info("checkQuantityMysql QuantityProducer data : {}", json);
        kafkaTemplate.send("check-quantity-Mysql", json);
    }

    public void checkQuantityRedis(QuantityDTO quantityDTO){
        String json = quantityDTO.toJson();
        log.info("checkQuantityRedis QuantityProducer data : {}", json);
        kafkaTemplate.send("check-quantity-Redis", json);
    }



    // 1. createOrder 실패 시, 롤백
    public void rollBackQuantity(QuantityDTO quantityDTO){
        String json = quantityDTO.toJson();
        log.info("rollBackQuantity QuantityProducer data : {}", json);
        kafkaTemplate.send("roll-back-quantity", json);
    }

    // 3-1 [Error] : 결제 실패 주문 삭제 및 재고 복구 롤백 진행
    public void rollBackOrderAndQuantity(QuantityDTO quantityDTO){
        String json = quantityDTO.toJson();
        log.info("rollBackOrderAndQuantity QuantityProducer data : {}", json);
        kafkaTemplate.send("payment-rollback", json);
    }

}
