package com.osio.orderservice.service;


import com.osio.orderservice.dto.OrderProductsListDTO;
import com.osio.orderservice.dto.OrdersListDTO;
import com.osio.orderservice.dto.QuantityDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {

//    // 주문 추가
//    @Transactional
//    Long createOrder(Long userId, OrderProductQuantityDTO orderProductQuantity, Long productId);



    // 주문 취소
    @Transactional
    String canceledOrder(Long userId, Long orders);

    // 주문 조회
    List<OrdersListDTO> getOrderList(Long userId);

    // 주문 번호 1의 상품 조회
    List<OrderProductsListDTO> getOrderProductsList(Long orderId, Long userId);

    // 주문 상태 변경
    @Transactional
    void updateOrderStatus();

}
