package com.osio.orderservice.dto;

import com.osio.orderservice.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdersListDTO {
    private Long orderId;
    private Long userId;
    private Timestamp orderDate;
    private Long orderTotalPrice;
    private Status status;
}
