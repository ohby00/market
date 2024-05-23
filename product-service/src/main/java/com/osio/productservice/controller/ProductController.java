package com.osio.productservice.controller;


import com.osio.productservice.dto.ProductDTO;
import com.osio.productservice.dto.QuantityDTO;
import com.osio.productservice.entity.Product;
import com.osio.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/product")
@CrossOrigin()
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    /* 상품 조회
         http://localhost8080/product/list
     */
    @GetMapping("/list")
    public ResponseEntity<List<ProductDTO>> productList(){
        List<ProductDTO> productList = productService.getAllProducts();
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    /* 상품 상세 조회
        http://localhost8080/detail/1
    */
    @GetMapping("/detail/{productId}")
    public ResponseEntity<ProductDTO> productDetail(@PathVariable("productId") Long productId) {
        ProductDTO detail = productService.getProductById(productId);

        return ResponseEntity.ok().body(detail);
    }

    /*  상품 등록
        http://localhost:8080/product/add

        {
        "productCategory" : "laptop",
        "productDetail" : "apple",
        "productImage" : "image",
        "productName" : "macbook",
        "productPrice" : 2000000,
        "productQuantity" : 5
        }
    */
    @PostMapping("/add")
    public ResponseEntity<Object> productAdd(@RequestBody ProductDTO ProductDTO,
                                             @RequestHeader("userId") Long userId) {
        log.info("controller add - userId = {}", userId);
        try {
            if (userId != 1L) {
                throw new RuntimeException("관리자가 아닙니다.");
            }
            Product product = productService.productAdd(ProductDTO);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/fix")
    public ResponseEntity<?> fixProductQuantity(@RequestBody QuantityDTO quantityDTO,
                                                @RequestHeader("userId") Long userId) {
        log.info("controller fix - userId = {}", userId);

        try {
            if (userId != 1L) {
                throw new RuntimeException("관리자가 아닙니다.");
            }

            Product product = productService.productFix(quantityDTO);
            if (product != null) {
                return ResponseEntity.ok(product);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }



//    // 곧 구현 예정
//    @DeleteMapping("/delete/{productId}")
//    public ResponseEntity<String> productDelete(@PathVariable("productId") Long productId) {
//        return ResponseEntity.status(HttpStatus.OK).body("Product deleted OK");
//    }

}
