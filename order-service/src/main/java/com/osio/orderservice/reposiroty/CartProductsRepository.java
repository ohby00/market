package com.osio.orderservice.reposiroty;

import com.osio.orderservice.entity.Cart;
import com.osio.orderservice.entity.CartProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartProductsRepository extends JpaRepository<CartProducts, Long> {

    CartProducts findByCartAndProductId(Cart cart, Long productId);

    CartProducts findByCartProductId(Long cartProductId);
}
