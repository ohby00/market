package com.osio.quantityservice.service;


import com.osio.quantityservice.dto.QuantityDTO;
import com.osio.quantityservice.dto.QuantityUpdateDTO;
import com.osio.quantityservice.entity.Quantity;
import com.osio.quantityservice.kafka.producer.OrderProducer;
import com.osio.quantityservice.repository.QuantityRepository;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
@Slf4j
@Transactional
public class QuantityServiceImpl implements QuantityService {

    private final QuantityRepository quantityRepository;
    private final RedisService redisService;
    private final RedissonClient redissonClient;
    private final OrderProducer orderProducer;

    public QuantityServiceImpl(QuantityRepository quantityRepository,
                               RedisService redisService,
                               RedissonClient redissonClient,
                               OrderProducer orderProducer) {
        this.quantityRepository = quantityRepository;
        this.redisService = redisService;
        this.redissonClient = redissonClient;
        this.orderProducer = orderProducer;
    }

//    @Override
//    public Long getProductQuantity(Long productId) {
//       return redisService.getQuantity(productId);
//    }

    @Override
// 재고 차감
    public ResponseEntity<String> decreaseQuantity(QuantityDTO quantityDTO) {
        // Redisson을 사용하여 분산 락 획득
        RLock lock = redissonClient.getLock("product_lock:" + quantityDTO.getProductId());
        lock.lock(); // 락 획득
        try {
            log.info("상품 ID {}의 재고 차감 진입", quantityDTO.getProductId());

            // 레디스에서 상품의 재고 확인
            Long availableQuantity = redisService.getQuantity(quantityDTO.getProductId());

            if (availableQuantity == null) {
                // 상품 정보 조회에 실패한 경우
                log.error("상품 ID {}의 정보 조회에 실패했습니다.", quantityDTO.getProductId());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("상품 정보 조회에 실패했습니다");
            }

            // 수량 부족 시 실패
            if (availableQuantity == 0 || quantityDTO.getQuantity() > availableQuantity) {
                log.info("상품 ID {}의 재고가 부족합니다", quantityDTO.getProductId());
                return ResponseEntity.badRequest().body("상품 재고가 부족합니다");
            }

            ResponseEntity<String> updateResponse = redisService.decreaseQuantity(quantityDTO.getProductId(), quantityDTO.getQuantity());

            if (updateResponse.getStatusCode() == HttpStatus.OK) {
                log.info("상품 ID {}의 차감된 재고: {}", quantityDTO.getProductId(), redisService.getQuantity(quantityDTO.getProductId()));
                return ResponseEntity.ok("상품 재고가 성공적으로 차감되었습니다");
            } else {
                // 상품 정보 업데이트에 실패한 경우
                log.error("상품 ID {}의 정보 업데이트에 실패했습니다.", quantityDTO.getProductId());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("재고 차감 중 오류가 발생했습니다");
            }

        } finally {
            lock.unlock(); // 락 해제
        }
    }


//    @Transactional
//    public String decreaseQuantity(QuantityDTO quantityDTO) {
//        Quantity quantity = quantityRepository.findByIdForUpdate(quantityDTO.getProductId());
//
//        if (quantity != null) {
//            long availableQuantity = quantity.getQuantity();
//            if (availableQuantity < quantityDTO.getQuantity()) {
//                log.info("상품의 재고가 부족합니다: {}", availableQuantity);
//                return "상품 재고가 부족합니다";
//            }
//
//            quantity.setQuantity(availableQuantity - quantityDTO.getQuantity());
//            quantityRepository.save(quantity);
//
//            log.info("차감된 재고: {}", quantity.getQuantity());
//            return "OK";
//        } else {
//            log.error("상품 정보 조회에 실패했습니다.");
//            throw new RuntimeException("상품 정보 조회에 실패했습니다");
//        }
//    }


//    @Transactional
//    // 3. 결제 진행 메소드
//    public void payment(QuantityDTO quantityDTO) {
//        orderProducer.payment(quantityDTO);
//    }

    // 1. createOrder
    public void createOrder(QuantityDTO quantityDTO) {
        orderProducer.createOrder(quantityDTO);
    }

    public void rollBackCreatedOrder(QuantityDTO quantityDTO) {
        orderProducer.rollbackCreatedOrder(String.valueOf(quantityDTO.getOrderId()));
        log.info("주문 제거 성공");
    }

    public void rollBackQuantity(QuantityDTO quantityDTO) {
        redisService.increaseQuantity(quantityDTO.getProductId(), quantityDTO.getQuantity());
        log.info("재고 복구 성공");
    }

    // 3-1 [Error] : 주문 삭제 및 재고 복구 완료
    public void rollBackOrderAndQuantity(QuantityDTO quantityDTO) {
        orderProducer.rollbackCreatedOrder(String.valueOf(quantityDTO.getOrderId()));
        redisService.increaseQuantity(quantityDTO.getProductId(), quantityDTO.getQuantity());
        log.info("재고 복구 및 주문 제거 성공");
    }

    public void updateQuantity(QuantityUpdateDTO quantityUpdateDTO) {
        // Update Quantity DB
        Quantity quantity = quantityRepository.findById(quantityUpdateDTO.getQuantityId()).orElse(null);

        if (quantity == null) {
            Quantity newQuantity = Quantity.builder()
                    .quantityId(quantityUpdateDTO.getQuantityId())
                    .quantity(quantityUpdateDTO.getQuantity())
                    .build();

            quantityRepository.save(newQuantity);

            redisService.setQuantity(newQuantity.getQuantityId(), newQuantity.getQuantity());

        } else {
            quantity.updateQuantity(quantity.getQuantity());

            redisService.setQuantity(quantity.getQuantityId(), quantity.getQuantity());
        }

    }

    public void rollBackQuantity(QuantityUpdateDTO quantityUpdateDTO) {
        // 둘다 삭제
        quantityRepository.deleteById(quantityUpdateDTO.getQuantityId());
        redisService.deleteQuantity(quantityUpdateDTO.getQuantityId());
    }

//    @Transactional
//    // 재고 DB 저장 - schedule
//    public void updateQuantity() {
//        List<Quantity> quantityList = quantityRepository.findAll();
//        for (Quantity quantity : quantityList) {
//            quantity.updateQuantity(redisService.getQuantity(quantity.getQuantityId()));
//        }
//    }
}