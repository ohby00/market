package com.osio.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private Timestamp orderDate;
    private Long orderTotalPrice;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Long userId;
    private Long productId;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
    private List<OrderProducts> orderProducts;

    public void updateStatus(Status orderStatus) {
        this.status = orderStatus;
    }
}
