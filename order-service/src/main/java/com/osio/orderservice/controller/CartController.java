package com.osio.orderservice.controller;

import com.osio.orderservice.dto.CartListDTO;
import com.osio.orderservice.dto.CartProductQuantityDTO;
import com.osio.orderservice.dto.CartUpdateDTO;
import com.osio.orderservice.dto.ProductDTO;
import com.osio.orderservice.entity.CartProducts;
import com.osio.orderservice.feign.ProductFeignClient;
import com.osio.orderservice.service.CartServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartServiceImpl cartServiceImpl;
    private final ProductFeignClient productOrderFeignClient;

    /* 장바구니 상품 조회
         http://localhost8080/cart/list
     */
    @GetMapping("/list")
    public ResponseEntity<List<CartListDTO>> getCartList(@RequestHeader("userId") Long userId) {
        try {
            List<CartListDTO> cartList = cartServiceImpl.getCartList(userId);
            return ResponseEntity.ok(cartList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    /* 장바구니 상품 추가
         http://localhost8080/cart/add/{productId}

         {
           cartProductQuantity : "1"
         }
    */
    @PostMapping("/add/{productId}")
    public ResponseEntity<String> addProductCart(@RequestHeader("userId") Long userId,
                                                 @PathVariable("productId") Long productId,
                                                 @RequestBody CartProductQuantityDTO cartProductQuantity) {
            ProductDTO product = productOrderFeignClient.getProductById(productId).getBody();
            String result = cartServiceImpl.addCartProduct(userId, productId, cartProductQuantity, product);
            return ResponseEntity.ok(result);
    }

    /* 장바구니 수량 수정
     http://localhost8080/cart/update

     {
    "productId" : 1,
    "cartProductQuantity" : 1,
    "cartId" : 1,
    "cartProductId" : 1
    }
    */
    @PatchMapping("/update")
    public ResponseEntity<Object> updateProductCart(@RequestHeader("userId") Long userId,
                                                    @RequestBody CartUpdateDTO cartUpdateDTO) {
        String result = cartServiceImpl.updateCartProduct(userId, cartUpdateDTO);
        return ResponseEntity.ok(result);
    }


    /* 장바구니 상품 삭제
     http://localhost8080/cart/delete

     {
    "cartProductId" : 1
    }
    */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteProductCart(@RequestHeader("userId") Long userId, @RequestBody CartProducts cartProducts) {
        try {
            String cartProduct = cartServiceImpl.deleteCartProduct(userId, cartProducts);
            return ResponseEntity.ok(cartProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete cart");
        }
    }
}
