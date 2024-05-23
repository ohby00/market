package com.osio.quantityservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quantity")
public class Quantity {
    @Id
    private Long quantityId;
    private Long quantity;

    public void updateQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
