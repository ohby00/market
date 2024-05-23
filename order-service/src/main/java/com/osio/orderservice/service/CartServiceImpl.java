package com.osio.orderservice.service;

import com.osio.orderservice.dto.CartListDTO;
import com.osio.orderservice.dto.CartProductQuantityDTO;
import com.osio.orderservice.dto.CartUpdateDTO;
import com.osio.orderservice.dto.ProductDTO;
import com.osio.orderservice.entity.Cart;
import com.osio.orderservice.entity.CartProducts;
import com.osio.orderservice.feign.ProductFeignClient;
import com.osio.orderservice.reposiroty.CartProductsRepository;
import com.osio.orderservice.reposiroty.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartProductsRepository cartProductsRepository;
    private final ProductFeignClient productOrderFeignClient;

    // 유저 장바구니 조회
    @Override
    public List<CartListDTO> getCartList(Long userId) {
        Optional<Cart> optionalCart = cartRepository.findById(userId);

        Cart cart = optionalCart.get();

        return cart.getCartProducts().stream()
                .map(cartProduct -> {
                    ProductDTO productDTO = productOrderFeignClient.getProductById(cartProduct.getProductId()).getBody();
                    if (productDTO != null) {
                        return CartListDTO.builder()
                                .userId(cart.getCartId())
                                .cartId(cartProduct.getCart().getCartId())
                                .productId(cartProduct.getProductId())
                                .cartProductId(cartProduct.getCartProductId())
                                .productName(productDTO.getProductName())
                                .cartProductPrice(cartProduct.getCartProductPrice())
                                .cartProductQuantity(cartProduct.getCartProductQuantity())
                                .build();
                    } else {
                        // ProductDTO가 null인 경우 처리
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 장바구니 상품 추가
    @Override
    @Transactional
    public String addCartProduct(Long userId,
                                 Long productId,
                                 CartProductQuantityDTO cartProductQuantity,
                                 ProductDTO product) {
        Cart cart = cartRepository.findById(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .cartId(userId)
                            .build();
                    return cartRepository.save(newCart);
                });

        if (cart.getCartProducts() == null) {
            cart.setCartProducts(new ArrayList<>()); // 초기화
        }

        // cartId : 1 에 Product가 존재하는지 판별
        CartProducts existCartProduct = cartProductsRepository.findByCartAndProductId(cart, productId);

        if (existCartProduct == null) {

            // Redis 제거 에러
//            ResponseEntity<String> checkQuantityResponse = checkQuantity(productId, cartProductQuantity.getCartProductQuantity());
//            if (checkQuantityResponse.getStatusCode() != HttpStatus.OK) {
//                return checkQuantityResponse.getBody();
//            }

            // CartProduct 저장
            CartProducts cartProducts = CartProducts.builder()
                    .cart(cart)
                    .productId(productId)
                    .cartProductQuantity(cartProductQuantity.getCartProductQuantity())
                    .cartProductPrice(product.getProductPrice() * cartProductQuantity.getCartProductQuantity())
                    .build();

            cartProductsRepository.save(cartProducts);

            updateCartTotalPrice(cart);

            return "장바구니 추가 완료";
        }
        return "이미 장바구니에 있는 상품입니다.";
    }

    @Override
    public String updateCartProduct(Long userId, CartUpdateDTO cartUpdateDTO) {
        Optional<Cart> optionalCart = cartRepository.findById(userId);
        if (optionalCart.isEmpty()) {
            // 사용자의 장바구니가 없는 경우 처리
            return "사용자의 장바구니를 찾을 수 없습니다.";
        }

        ResponseEntity<ProductDTO> productResponse = productOrderFeignClient.getProductById(cartUpdateDTO.getProductId());
            ProductDTO product = productResponse.getBody();
            CartProducts cartProduct = cartProductsRepository.findByCartProductId(cartUpdateDTO.getCartProductId());

            if (cartProduct != null) {

                // 재고 확인
                // Redis 제거 에러
//                ResponseEntity<String> checkQuantityResponse = checkQuantity(product.getProductId(), cartUpdateDTO.getCartProductQuantity());
//                if (checkQuantityResponse.getStatusCode() != HttpStatus.OK) {
//                    return checkQuantityResponse.getBody();
//                }

                // 장바구니 상품 수량 Update
                Long price = cartUpdateDTO.getCartProductQuantity() * product.getProductPrice();
                cartProduct.setCartProductQuantity(cartUpdateDTO.getCartProductQuantity());
                cartProduct.setCartProductPrice(price);
                cartProductsRepository.save(cartProduct);

                return "수정 완료";
            } else {
                return "해당 장바구니 상품을 찾을 수 없습니다.";
            }
    }

    // 장바구니 상품 삭제
    @Override
    public String deleteCartProduct(Long userId, CartProducts cartProductId){
        Optional<CartProducts> cartProducts = cartProductsRepository.findById(userId);

        if (cartProductsRepository.findByCartProductId(cartProductId.getCartProductId()) != null) {
            cartProductsRepository.delete(cartProductId);
            return "상품 제거 완료";
        }
        return "제거할 상품이 없습니다.";
    }

    // 장바구니 전체 가격 업데이트
    private void updateCartTotalPrice(Cart cart) {
        List<CartProducts> cartProducts = cart.getCartProducts();
        long totalPrice = 0;
        for (CartProducts product : cartProducts) {
            totalPrice += product.getCartProductPrice();
        }
        cart.setCartTotalPrice(totalPrice);
        cartRepository.save(cart);
    }

    // Redis 제거 에러
//    @Transactional
//    public ResponseEntity<String> checkQuantity(Long productId, Long cartQuantity) {
        // 레디스에서 상품의 재고 확인
//        Long availableQuantity = redisService.getQuantity(productId);
//        if (availableQuantity == null || availableQuantity < cartQuantity) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("재고가 부족합니다.");
//        } else {
//            return ResponseEntity.ok("재고가 충분합니다.");
//        }
//    }
}