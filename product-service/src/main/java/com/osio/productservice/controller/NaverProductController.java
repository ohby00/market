//package com.osio.productservice.controller;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.osio.productservice.service.NaverProductServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class NaverProductController {
//
//    private final NaverProductServiceImpl naverProductService;
//
//    @Autowired
//    public NaverProductController(NaverProductServiceImpl naverProductService) {
//        this.naverProductService = naverProductService;
//    }
//
//    @GetMapping("/fetch-products")
//    public String fetchProducts(@RequestParam(name = "query") String query) throws JsonProcessingException {
//        naverProductService.fetchAndSaveProducts(query);
//        return "Products fetched and saved successfully!";
//    }
//}
