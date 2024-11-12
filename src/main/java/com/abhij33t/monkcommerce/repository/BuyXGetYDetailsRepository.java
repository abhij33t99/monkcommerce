package com.abhij33t.monkcommerce.repository;

import com.abhij33t.monkcommerce.model.BuyXGetYDetails;
import com.abhij33t.monkcommerce.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuyXGetYDetailsRepository extends JpaRepository<BuyXGetYDetails, Integer> {
    Optional<BuyXGetYDetails> findByCoupon(Coupon coupon);
}
