package com.osio.orderservice.reposiroty;


import com.osio.orderservice.entity.OrderProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProducts, Long> {
    // 주문 ID에 해당하는 모든 OrderProducts를 삭제하는 메서드
    void deleteByOrders_OrderId(Long orderId);

    // orderId로 찾기
    Optional<OrderProducts> findByOrders_OrderId(Long orderId);
}
