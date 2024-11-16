package com.abhij33t.monkcommerce.service;

import com.abhij33t.monkcommerce.dto.CouponTypeDto;
import com.abhij33t.monkcommerce.strategy.addCouponStrategy.AddBxGyCouponImpl;
import com.abhij33t.monkcommerce.strategy.addCouponStrategy.AddCartCouponImpl;
import com.abhij33t.monkcommerce.strategy.addCouponStrategy.AddCouponStrategy;
import com.abhij33t.monkcommerce.strategy.addCouponStrategy.AddProductCouponImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponStrategyFactory {
    private final AddCartCouponImpl addCartCouponImpl;
    private final AddProductCouponImpl addProductCouponImpl;
    private final AddBxGyCouponImpl addBxGyCouponImpl;

    public AddCouponStrategy getStrategy(CouponTypeDto type) {
        return switch (type) {
            case CART -> addCartCouponImpl;
            case PRODUCT -> addProductCouponImpl;
            case BUY_GET -> addBxGyCouponImpl;
        } ;
    }
}
