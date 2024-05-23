package com.osio.productservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osio.productservice.entity.NaverProduct;
import com.osio.productservice.repository.NaverProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class NaverProductServiceImpl {

    private final NaverProductRepository naverProductRepository;

    @Value("${naver-api.client-id}")
    private String clientId;

    @Value("${naver-api.client-secret}")
    private String clientSecret;

    @Autowired
    public NaverProductServiceImpl(NaverProductRepository naverProductRepository) {
        this.naverProductRepository = naverProductRepository;
    }

    public void fetchAndSaveProducts(String query) throws JsonProcessingException {
        int display = 100; // 한 번에 가져올 최대 아이템 수
        int start = 1; // 시작 지점
        boolean hasMore = true;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        ObjectMapper objectMapper = new ObjectMapper();

        while (hasMore && start <= 1000) {
            String url = String.format("https://openapi.naver.com/v1/search/shop.json?query=%s&display=%d&start=%d", query, display, start);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCodeValue() == 200) {
                String responseBody = response.getBody();
                JsonNode items = objectMapper.readTree(responseBody).get("items");

                List<NaverProduct> products = new ArrayList<>();
                for (JsonNode item : items) {
                    NaverProduct naverProduct = new NaverProduct();
                    naverProduct.setTitle(item.get("title").asText());
                    naverProduct.setImage(item.get("image").asText());
                    naverProduct.setLink(item.get("link").asText());
                    naverProduct.setPrice(item.get("lprice").asInt());
                    naverProduct.setMallName(item.get("mallName").asText());
                    products.add(naverProduct);
                }

                naverProductRepository.saveAll(products);

                start += display; // 다음 페이지로 이동
                hasMore = items.size() == display; // 현재 페이지에 아이템이 모두 채워졌으면 계속
            } else {
                throw new RuntimeException("Failed to fetch products: " + response.getStatusCode());
            }
        }
    }
}