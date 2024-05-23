package com.osio.orderservice.reposiroty;

import com.osio.orderservice.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
        Optional<Orders> findByUserIdAndOrderId(Long userId, Long orderId);

        List<Orders> findByUserId(Long userId);
}
