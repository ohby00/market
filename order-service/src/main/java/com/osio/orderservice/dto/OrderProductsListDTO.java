package com.osio.orderservice.dto;

import com.osio.orderservice.entity.Orders;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductsListDTO {
    private Long orderId;
    private Long orderProductId;
    private Long orderProductQuantity;
    private Long orderProductPrice;
}
