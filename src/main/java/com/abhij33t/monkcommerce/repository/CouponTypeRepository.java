package com.abhij33t.monkcommerce.repository;

import com.abhij33t.monkcommerce.dto.CouponTypeDto;
import com.abhij33t.monkcommerce.model.CouponType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponTypeRepository extends JpaRepository<CouponType, Integer> {
    Optional<CouponType> findByName(CouponTypeDto name);
}
