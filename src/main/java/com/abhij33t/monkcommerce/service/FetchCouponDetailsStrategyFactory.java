package com.abhij33t.monkcommerce.service;

import com.abhij33t.monkcommerce.dto.CouponTypeDto;
import com.abhij33t.monkcommerce.strategy.fetchCouponDetailsStrategy.FetchBuyXGetYCouponDetails;
import com.abhij33t.monkcommerce.strategy.fetchCouponDetailsStrategy.FetchCartCouponDetails;
import com.abhij33t.monkcommerce.strategy.fetchCouponDetailsStrategy.FetchCouponDetailsStrategy;
import com.abhij33t.monkcommerce.strategy.fetchCouponDetailsStrategy.FetchProductCouponDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FetchCouponDetailsStrategyFactory {

    private final FetchCartCouponDetails fetchCartCouponDetails;
    private final FetchProductCouponDetails fetchProductCouponDetails;
    private final FetchBuyXGetYCouponDetails fetchBxGyCouponDetails;

    public FetchCouponDetailsStrategy getStrategy(CouponTypeDto type) {
        return switch (type) {
            case CART -> fetchCartCouponDetails;
            case PRODUCT -> fetchProductCouponDetails;
            case BUY_GET -> fetchBxGyCouponDetails;
        };
    }
}
