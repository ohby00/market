package com.osio.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartListDTO {
    private Long userId;
    private Long cartId;
    private Long productId;
    private Long cartProductId;
    private String productName;

    private Long cartProductPrice;
    private Long cartProductQuantity;
}
