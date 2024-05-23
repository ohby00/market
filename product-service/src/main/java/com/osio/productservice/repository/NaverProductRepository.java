package com.osio.productservice.repository;

import com.osio.productservice.entity.NaverProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NaverProductRepository extends JpaRepository<NaverProduct, Long> {

}
