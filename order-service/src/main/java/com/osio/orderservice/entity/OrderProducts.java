package com.osio.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orderProducts")
public class OrderProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderProductsId;
    private Long orderProductQuantity;
    private Long orderProductPrice;

    private Long productId;

    private Long userId;

    @ManyToOne
    @JoinColumn(name="orderId")
    private Orders orders;
}
