package com.osio.orderservice.service;

import com.osio.orderservice.config.FeignConfig.CustomFeignClientException;
import com.osio.orderservice.dto.*;
import com.osio.orderservice.entity.OrderProducts;
import com.osio.orderservice.entity.Orders;
import com.osio.orderservice.entity.Status;
import com.osio.orderservice.feign.ProductFeignClient;
import com.osio.orderservice.feign.QuantityFeignClient;
import com.osio.orderservice.kafka.producer.QuantityProducer;
import com.osio.orderservice.reposiroty.OrderProductRepository;
import com.osio.orderservice.reposiroty.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductFeignClient productFeignClient;
    private final QuantityProducer quantityProducer;
    private final MessageService messageService;

    // 주문 조회
    @Override
    public List<OrdersListDTO> getOrderList(Long userId) {
        log.info("userId={}", userId);

        List<Orders> orders;
        if (userId == 1) {
            // 모든 주문 내역을 가져오기
            orders = orderRepository.findAll();
        } else {
            // 특정 userId에 해당하는 모든 주문 내역을 가져오기
            orders = orderRepository.findByUserId(userId);
        }

        // OrdersListDTO로 변환하여 리스트로 반환
        return orders.stream()
                .map(order -> OrdersListDTO.builder()
                        .orderId(order.getOrderId())
                        .userId(order.getUserId()) // userId 추가
                        .orderDate(order.getOrderDate())
                        .orderTotalPrice(order.getOrderTotalPrice())
                        .status(order.getStatus())
                        .build())
                .collect(Collectors.toList());
}


    // 주문 번호 1의 상품 조회
    @Override
    public List<OrderProductsListDTO> getOrderProductsList(Long orderId, Long userId) {
        Optional<Orders> order = orderRepository.findByUserIdAndOrderId(userId, orderId);

        if (order.isPresent()) {
            Orders orders = order.get();
            List<OrderProducts> orderProducts = orders.getOrderProducts();
            List<OrderProductsListDTO> orderProductsList = new ArrayList<>();

            for (OrderProducts orderProduct : orderProducts) {
                orderProductsList.add(OrderProductsListDTO.builder()
                        .orderId(orderProduct.getOrders().getOrderId())
                        .orderProductId(orderProduct.getOrderProductsId())
                        .orderProductQuantity(orderProduct.getOrderProductQuantity())
                        .orderProductPrice(orderProduct.getOrderProductPrice())
                        .build());
            }
            return orderProductsList;
        } else {
            // 주어진 orderId에 해당하는 주문이 존재하지 않음
            // 예외 처리 또는 다른 작업 수행
            return Collections.emptyList(); // 또는 예외를 throw
        }
    }


    // 주문 상태 변경
    @Override
    @Transactional
    public void updateOrderStatus() {
        List<Orders> orders = orderRepository.findAll();
        LocalDateTime now = LocalDateTime.now(); // 현재 시간

        for (Orders order : orders) {
            LocalDateTime orderDate = order.getOrderDate().toLocalDateTime();

            // 날짜 차이
            long days = Duration.between(orderDate, now).toDays();

            if (order.getStatus() == Status.PAY_SUCCESS) {
                if (days == 1) { // 1일
                    order.updateStatus(Status.READY_TO_SHIPPING);
                } else if (days == 2) { // 2일
                    order.updateStatus(Status.SHIPPING);
                } else if (days == 3) { // 3일
                    order.updateStatus(Status.DELIVERED);
                }
            }
        }
    }


    // 주문 취소
    @Override
    public String canceledOrder(Long userId, Long orderId) {
        log.info("취소 진입");
        Optional<Orders> order = orderRepository.findByUserIdAndOrderId(userId, orderId);

        if (order.isPresent()) {
            ProductDTO product = productFeignClient.getProductById(order.get().getProductId()).getBody();
            if (order.get().getStatus() == Status.READY_TO_SHIPPING || order.get().getStatus() == Status.PAY_SUCCESS) {
                for (OrderProducts orderProduct : order.get().getOrderProducts()) {
                    order.get().updateStatus(Status.CANCELED);
                    // Redis 제거 에러
//                    redisService.increaseQuantity(orderProduct.getProductId(), orderProduct.getOrderProductQuantity());
                }
            } else {
                return "주문 취소 불가";
            }
            return "주문 취소 완료";
        }
        return "주문 내역이 없습니다.";
    }


    // 주문 추가 (장바구니 상품이 아닌 상품 직접 구매)
    @Transactional
    public ResponseEntity<String> createOrderMysql(Long userId, OrderProductQuantityDTO orderProductQuantity, Long productId) {
        log.info("createOrderMysql(단일 주문) -> userId={}, Quantity={}, productId={}",
                userId, orderProductQuantity.getOrderProductQuantity(), productId);

        // productId로 상품 찾기
        ProductDTO product = productFeignClient.getProductById(productId).getBody();
        if (product == null) {
            return ResponseEntity.ok("상품을 찾을 수 없습니다.");
        }

        QuantityDTO quantityDTO = QuantityDTO.builder()
                .userId(userId)
                .productId(productId)
                .productPrice(product.getProductPrice())
                .quantity(orderProductQuantity.getOrderProductQuantity())
                .build();

        try {
            quantityProducer.checkQuantityMysql(quantityDTO);


            // 현재 날짜와 시간을 가져옵니다.
            LocalDateTime now = LocalDateTime.now();

            // LocalDateTime 객체를 Timestamp 객체로 변환합니다.
            Timestamp orderDate = Timestamp.valueOf(now);

            // 주문 생성
            Orders order = Orders.builder()
                    .userId(userId)
                    .productId(productId)
                    .orderTotalPrice(product.getProductPrice() * orderProductQuantity.getOrderProductQuantity()) // 주문 총 가격 설정
                    .orderDate(orderDate)
                    .status(Status.PENDING) // 결제 대기 중 상태로 설정
                    .build();

            // 주문 상품 생성
            OrderProducts orderProduct = OrderProducts.builder()
                    .userId(userId)
                    .productId(productId)
                    .orderProductQuantity(orderProductQuantity.getOrderProductQuantity())
                    .orderProductPrice(product.getProductPrice() * orderProductQuantity.getOrderProductQuantity())
                    .orders(order)
                    .build();

            // 주문 저장
            orderRepository.save(order);
            orderProductRepository.save(orderProduct);

            return ResponseEntity.ok("주문이 성공적으로 생성되었습니다.");
        } catch (CustomFeignClientException e) {
            log.error("Feign 클라이언트 오류: status={}, message={}", e.getStatus(), e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        } catch (Exception e) {
            log.error("주문 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("주문 생성 중 오류가 발생했습니다.");
        }
    }


    // 주문 추가 (장바구니 상품이 아닌 상품 직접 구매)
    @Transactional
    public ResponseEntity<String> createOrderRedis(Long userId, OrderProductQuantityDTO orderProductQuantity, Long productId) {
        log.info("createOrderRedis(단일 주문) -> userId={}, Quantity={}, productId={}",
                userId, orderProductQuantity.getOrderProductQuantity(), productId);

        // productId로 상품 찾기
        ProductDTO product = productFeignClient.getProductById(productId).getBody();
        if (product == null) {
            return ResponseEntity.ok("상품을 찾을 수 없습니다.");
        }

        QuantityDTO quantityDTO = QuantityDTO.builder()
                .userId(userId)
                .productId(productId)
                .productPrice(product.getProductPrice())
                .quantity(orderProductQuantity.getOrderProductQuantity())
                .build();

            quantityProducer.checkQuantityRedis(quantityDTO);


            // 현재 날짜와 시간을 가져옵니다.
            LocalDateTime now = LocalDateTime.now();

            // LocalDateTime 객체를 Timestamp 객체로 변환합니다.
            Timestamp orderDate = Timestamp.valueOf(now);

            // 주문 생성
            Orders order = Orders.builder()
                    .userId(userId)
                    .productId(productId)
                    .orderTotalPrice(product.getProductPrice() * orderProductQuantity.getOrderProductQuantity()) // 주문 총 가격 설정
                    .orderDate(orderDate)
                    .status(Status.PENDING) // 결제 대기 중 상태로 설정
                    .build();

            // 주문 상품 생성
            OrderProducts orderProduct = OrderProducts.builder()
                    .userId(userId)
                    .productId(productId)
                    .orderProductQuantity(orderProductQuantity.getOrderProductQuantity())
                    .orderProductPrice(product.getProductPrice() * orderProductQuantity.getOrderProductQuantity())
                    .orders(order)
                    .build();

            // 주문 저장
            orderRepository.save(order);
            orderProductRepository.save(orderProduct);
            return ResponseEntity.ok("주문이 성공적으로 생성되었습니다.");
        }

    // 결제 진입
    @Transactional
    public ResponseEntity<String> processPayment(Long userId, Long orderId) {


        Optional<Orders> ordersOptional = orderRepository.findByUserIdAndOrderId(userId, orderId);

        Optional<OrderProducts> product = orderProductRepository.findByOrders_OrderId(orderId);

        QuantityDTO quantityDTO = QuantityDTO.builder()
                .productId(ordersOptional.get().getProductId())
                .userId(userId)
                .quantity(product.get().getOrderProductQuantity())
                .orderId(orderId)
                .build();

        // 주문을 찾습니다.
        Optional<Orders> orderOptional = orderRepository.findByUserIdAndOrderId(quantityDTO.getUserId(), quantityDTO.getOrderId());

        if (orderOptional.isPresent()) {
            Orders order = orderOptional.get();

            // 이미 결제된 주문인 경우 처리하지 않음
            if (order.getStatus() == Status.PAY_SUCCESS) {
                log.info("이미 결제된 주문입니다.");
                return ResponseEntity.badRequest().body("이미 결제된 주문입니다.");
            }

            // 결제 진행
            log.info("결제를 진행합니다.");

            ResponseEntity<String> paymentResponse = orderPayment(quantityDTO);

            // 결제 상태에 따라 적절한 ResponseEntity 반환
            if (paymentResponse.getStatusCode() == HttpStatus.OK) {
                log.info("결제가 완료되었습니다.");
                return paymentResponse;
            } else {
                log.info("결제를 처리하는 중에 오류가 발생했습니다.");
                return paymentResponse;
            }

        } else {
            log.info("주문을 찾을 수 없습니다.");
            return ResponseEntity.badRequest().body("주문을 찾을 수 없습니다.");
        }
    }


    // 결제 중
    @Transactional
    public ResponseEntity<String> orderPayment(QuantityDTO quantityDTO){

        Optional<Orders> optionalOrder = orderRepository.findByUserIdAndOrderId(quantityDTO.getUserId(), quantityDTO.getOrderId());
        Orders order = optionalOrder.get();

        // 상태 변경 후 DB저장
        order.setStatus(Status.PAY_REQUIRED);
        orderRepository.save(order);

        // 20%의 확률로 고객이 이탈
        // 0 ~ 1 사이의 랜덤한 수를 받아서 0.2 이하일 때, 고객 이탈 가정
        if (Math.random() < 0.2) {
            log.info("고객 이탈 유저 아이디 : {}", order.getUserId());
            quantityProducer.rollBackOrderAndQuantity(quantityDTO);
            return ResponseEntity.badRequest().body("고객 이탈");
        }

        log.info("고객 이탈 통과 -> 결제 진행 userId = {}", order.getUserId());

        // 20%의 확률로 고객이 결제 실패
        // 0 ~ 1 사이의 랜덤한 수를 받아서 0.2 이하일 때, 결제 실패 가정
        if (Math.random() < 0.2) {
            log.info("결제 실패 유저 아이디 = {}",order.getUserId());
            quantityProducer.rollBackOrderAndQuantity(quantityDTO);
            return ResponseEntity.badRequest().body("결제 실패");
        }

        order.setStatus(Status.PAY_SUCCESS);
        log.info("주문 -> 결제 성공 userId = {}", order.getUserId());

        // 주문 저장
        orderRepository.save(order);

        return ResponseEntity.ok("결제 완료");
    }


    @Transactional
    // 주문 취소
    // 2-1 [Error] : 주문 삭제 완료
    public void deleteOrder(Long orderId) {
        // 모든 OrderProducts 삭제합니다.
        orderProductRepository.deleteByOrders_OrderId(orderId);
        orderRepository.deleteById(orderId);
    }

    @PostMapping("/send/message")
    public ResponseEntity<String> sendMessage(@RequestBody MessageDto messageDto) {
        this.messageService.sendMessage(messageDto);
        return ResponseEntity.ok("Message send to ActiveMQ!");
    }
}



