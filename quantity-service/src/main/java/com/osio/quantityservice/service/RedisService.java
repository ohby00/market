package com.osio.quantityservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setQuantity(Long productId, Long quantity) {
        log.info("레디스 수량 추가 -> product id: {} quantity: {}", productId, quantity);
        redisTemplate.opsForValue().set(productId.toString(), quantity.toString());
    }

    public Long getQuantity(Long productId) {
        String quantityString = redisTemplate.opsForValue().get(productId.toString());
        if (quantityString != null) {
            return Long.parseLong(quantityString);
        } else {
            return null;
        }
    }

//    public Map<String, String> getAllKeysAndValuesFromRedis() {
//        // 레디스 모든 키 가져오기
//        Set<String> keys = redisTemplate.keys("*");
//
//        // 각 키에 해당하는 값을 맵에 추가
//        Map<String, String> keyValueMap = new HashMap<>();
//        for (String key : keys) {
//            keyValueMap.put(key, redisTemplate.opsForValue().get(key));
//        }
//
//        return keyValueMap;
//    }

    public ResponseEntity<String> decreaseQuantity(Long productId, Long quantity) {
        redisTemplate.opsForValue().decrement(productId.toString(), quantity);
        return ResponseEntity.ok(quantity.toString());
    }

    public void increaseQuantity(Long productId, Long quantity) {
        redisTemplate.opsForValue().increment(productId.toString(), quantity);
    }

    // 해당 키,값 삭제
    public void deleteQuantity(Long productId) {
        redisTemplate.delete(productId.toString());
    }
}
