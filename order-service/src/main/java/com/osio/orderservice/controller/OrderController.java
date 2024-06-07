package com.osio.orderservice.controller;

import com.osio.orderservice.dto.OrderProductQuantityDTO;
import com.osio.orderservice.dto.OrderProductsListDTO;
import com.osio.orderservice.dto.OrdersListDTO;
import com.osio.orderservice.service.OrderServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@CrossOrigin
@Slf4j
@AllArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderServiceImpl;

    @GetMapping("/list")
    public ResponseEntity<List<OrdersListDTO>> getOrdersList(@RequestHeader("userId") Long userId) {
        try {
            List<OrdersListDTO> orderList = orderServiceImpl.getOrderList(userId);
            return ResponseEntity.ok(orderList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/list/{orderId}")
    public ResponseEntity<List<OrderProductsListDTO>> getOrderProductQuantity(@PathVariable("orderId") Long orderId,
                                                                              @RequestHeader("userId") Long userId) {
        try {
            List<OrderProductsListDTO> orderProductList = orderServiceImpl.getOrderProductsList(orderId, userId);
            return ResponseEntity.ok(orderProductList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/create/mysql/{productId}")
    public ResponseEntity<String> addOrderMysql(
            @RequestHeader("userId") Long userId,
            @RequestBody OrderProductQuantityDTO orderProductQuantity,
            @PathVariable("productId") Long productId) {

        ResponseEntity<String> response = orderServiceImpl.createOrderMysql(userId, orderProductQuantity, productId);
        return ResponseEntity.ok(response.getBody());
    }

    @PostMapping("/create/redis/{productId}")
    public ResponseEntity<String> addOrderRedis(
            @RequestHeader("userId") Long userId,
            @RequestBody OrderProductQuantityDTO orderProductQuantity,
            @PathVariable("productId") Long productId) {

        ResponseEntity<String> response = orderServiceImpl.createOrderRedis(userId, orderProductQuantity, productId);
        return ResponseEntity.ok(response.getBody());
    }


    // 결제 진입
    @PostMapping("/payment/{orderId}")
    public ResponseEntity<String> payOrder(
           @RequestHeader("userId") Long userId,
           @PathVariable("orderId") Long orderId) {

        return orderServiceImpl.processPayment(userId, orderId);
    }


    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<String> canceledOrder(@RequestHeader("userId") Long userId,
                                                @PathVariable("orderId") Long orderId) {
            String order = orderServiceImpl.canceledOrder(userId, orderId);
            return ResponseEntity.ok(order);
    }
}
