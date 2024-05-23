package com.osio.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartUpdateDTO {
    private Long cartId;
    private Long cartProductId;
    private Long cartProductQuantity;
    private Long productId;
}
