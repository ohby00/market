package com.osio.productservice.service;

import com.osio.productservice.dto.ProductDTO;
import com.osio.productservice.dto.QuantityDTO;
import com.osio.productservice.dto.QuantityUpdateDTO;
import com.osio.productservice.entity.Product;
import com.osio.productservice.kafka.producer.QuantityProducer;
import com.osio.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final QuantityProducer quantityProducer;

    /* 상품 조회
       단순히 데이터를 노출시키는 경우 DTO로 빌드
       stream에서 처리한 요소를 리스트 형태로 처리하기 위해 collect를 사용하고,
       findAll을 사용하기 위해 List로 리턴값 지정
    */
    @Override
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();

        // ProductMapper를 사용하여 Product 객체를 ProductDTO로 변환
        List<ProductDTO> list = products.stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
        return list;
    }

    /* 상품 상세 조회
       람다식을 쓰지 않으면 NoSuchElementException 사용 불가 ?
    */
    @Override
    public ProductDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(()
                -> new NoSuchElementException("No item" + productId));
        return ProductMapper.toDTO(product);
    }

    // 상품 등록
    // 데이터의 수정이 있을 경우 엔티티로 빌드
    @Override
    public Product productAdd(ProductDTO productDTO) {
        Product product = ProductMapper.toEntity(productDTO);
        productRepository.save(product);

        QuantityUpdateDTO quantityUpdateDTO = QuantityUpdateDTO.builder()
                .quantityId(product.getProductId())
                .quantity(productDTO.getProductQuantity())
                .build();

        quantityProducer.send(quantityUpdateDTO);
        return product;
    }

    // 상품 수량 추가
    @Override
    public Product productFix(QuantityDTO quantityDTO) {
        Optional<Product> findProduct = productRepository.findById(quantityDTO.getProductId());
        findProduct.ifPresent(product -> {

            QuantityUpdateDTO quantityUpdateDTO = QuantityUpdateDTO.builder()
                    .quantityId(quantityDTO.getProductId())
                    .quantity(quantityDTO.getQuantity())
                    .build();

            quantityProducer.send(quantityUpdateDTO);
        });
        return findProduct.orElse(null);
    }

    // feign client
    @Override
    public ProductDTO getProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow();
        return ProductMapper.toDTO(product);
    }


    private static class ProductMapper {

        public static ProductDTO toDTO(Product product) {
            return ProductDTO.builder()
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .productCategory(product.getProductCategory())
                    .productPrice(product.getProductPrice())
                    .productDetail(product.getProductDetail())
                    .productImage(product.getProductImage())
                    .build();
        }

        public static Product toEntity(ProductDTO productDTO) {
            return Product.builder()
                    .productId(productDTO.getProductId())
                    .productName(productDTO.getProductName())
                    .productCategory(productDTO.getProductCategory())
                    .productPrice(productDTO.getProductPrice())
                    .productDetail(productDTO.getProductDetail())
                    .productImage(productDTO.getProductImage())
                    .build();
        }
    }
}