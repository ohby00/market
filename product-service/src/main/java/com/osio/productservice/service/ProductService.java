package com.osio.productservice.service;

import com.osio.productservice.dto.ProductDTO;
import com.osio.productservice.dto.QuantityDTO;
import com.osio.productservice.entity.Product;

import java.util.List;

public interface ProductService {
    // 상품 상세 조회
    ProductDTO getProductById(Long productId);

    // 상품 등록
    Product productAdd(ProductDTO productAddDTO);

    // 상품 수정(수량 추가, 가격 조정 등등)
    Product productFix(QuantityDTO quantityDTO);

    // 상품 조회
    List<ProductDTO> getAllProducts();

    // feign client
    ProductDTO getProduct(Long productId);
}
