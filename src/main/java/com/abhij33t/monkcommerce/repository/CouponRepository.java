package com.abhij33t.monkcommerce.repository;

import com.abhij33t.monkcommerce.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
}
