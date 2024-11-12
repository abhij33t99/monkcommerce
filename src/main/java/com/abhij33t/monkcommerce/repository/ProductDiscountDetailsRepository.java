package com.abhij33t.monkcommerce.repository;

import com.abhij33t.monkcommerce.model.Coupon;
import com.abhij33t.monkcommerce.model.Product;
import com.abhij33t.monkcommerce.model.ProductDiscountDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductDiscountDetailsRepository extends JpaRepository<ProductDiscountDetails, Integer> {
    Optional<ProductDiscountDetails> findByCoupon(Coupon coupon);
    Optional<ProductDiscountDetails> findByProduct(Product product);
}
