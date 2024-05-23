package com.osio.orderservice.service;

import com.osio.orderservice.dto.CartListDTO;
import com.osio.orderservice.dto.CartProductQuantityDTO;
import com.osio.orderservice.dto.CartUpdateDTO;
import com.osio.orderservice.dto.ProductDTO;
import com.osio.orderservice.entity.CartProducts;

import java.util.List;

public interface CartService {
    // 사용자 장바구니 조회
    List<CartListDTO> getCartList(Long userId);

    // 장바구니 상품 추가
    String addCartProduct(Long userId, Long productId, CartProductQuantityDTO cartProductQuantity, ProductDTO product);

    // 장바구니 수량 변경
    String updateCartProduct(Long userId, CartUpdateDTO cartUpdateDTO);

    // 장바구니 상품 제거
    String deleteCartProduct(Long userId, CartProducts cartProductId);
}
