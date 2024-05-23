package com.osio.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cart")
public class Cart {
    @Id
    private Long cartId;
    private Long cartTotalPrice;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL )
    private List<CartProducts> cartProducts;
}
