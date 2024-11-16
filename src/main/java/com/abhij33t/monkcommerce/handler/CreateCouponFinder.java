package com.abhij33t.monkcommerce.handler;

import com.abhij33t.monkcommerce.dto.CouponTypeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateCouponFinder {

    private final CartCouponFinder cartCouponFinder;
    private final ProductCouponFinder productCouponFinder;
    private final BuyXGetYCouponFinder buyXGetYCouponFinder;

    public CouponFinder createCouponFinder(CouponTypeDto type) {
        return switch (type) {
            case CART -> cartCouponFinder;
            case PRODUCT -> productCouponFinder;
            case BUY_GET -> buyXGetYCouponFinder;
        };
    }
}
