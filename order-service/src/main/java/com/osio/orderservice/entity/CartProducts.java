package com.osio.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cartProducts")
public class CartProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartProductId;
    private Long cartProductQuantity;
    private Long cartProductPrice;

    private Long productId;

    @ManyToOne
    @JoinColumn(name="cartId")
    private Cart cart;
}