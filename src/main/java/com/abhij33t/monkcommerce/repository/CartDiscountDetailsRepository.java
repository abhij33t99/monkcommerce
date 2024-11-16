package com.abhij33t.monkcommerce.repository;

import com.abhij33t.monkcommerce.model.CartDiscountDetails;
import com.abhij33t.monkcommerce.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartDiscountDetailsRepository extends JpaRepository<CartDiscountDetails, Integer> {
    Optional<CartDiscountDetails> findByCoupon(Coupon coupon);
    List<CartDiscountDetails> findByThresholdLessThan(Double threshold);

    void deleteByCoupon(Coupon coupon);
}
