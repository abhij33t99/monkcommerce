package com.abhij33t.monkcommerce.service;

import com.abhij33t.monkcommerce.dto.CouponTypeDto;
import com.abhij33t.monkcommerce.strategy.applyCouponStrategy.ApplyBxGyCouponImpl;
import com.abhij33t.monkcommerce.strategy.applyCouponStrategy.ApplyCartCouponImpl;
import com.abhij33t.monkcommerce.strategy.applyCouponStrategy.ApplyCouponStrategy;
import com.abhij33t.monkcommerce.strategy.applyCouponStrategy.ApplyProductCouponImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplyCouponStrategyFactory {

    private final ApplyCartCouponImpl applyCartCouponImpl;
    private final ApplyProductCouponImpl applyProductCouponImpl;
    private final ApplyBxGyCouponImpl applyBxGyCouponImpl;

    public ApplyCouponStrategy getStrategy(CouponTypeDto type) {
        return switch (type) {
            case CART -> applyCartCouponImpl;
            case PRODUCT -> applyProductCouponImpl;
            case BUY_GET -> applyBxGyCouponImpl;
        };
    }
}
