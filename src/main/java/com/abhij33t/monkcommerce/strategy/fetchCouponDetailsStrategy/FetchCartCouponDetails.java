package com.abhij33t.monkcommerce.strategy.fetchCouponDetailsStrategy;

import com.abhij33t.monkcommerce.dto.CartWiseDetails;
import com.abhij33t.monkcommerce.dto.CouponDto;
import com.abhij33t.monkcommerce.dto.CouponTypeDto;
import com.abhij33t.monkcommerce.model.Coupon;
import com.abhij33t.monkcommerce.repository.CartDiscountDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FetchCartCouponDetails implements FetchCouponDetailsStrategy {

    private final CartDiscountDetailsRepository cartDiscountDetailsRepository;

    @Override
    public CouponDto fetchCouponDetails(Coupon coupon) {
        var cartDiscountDetails = cartDiscountDetailsRepository.findByCoupon(coupon).get();
        return CouponDto.builder()
                .type(CouponTypeDto.CART)
                .details(CartWiseDetails.builder()
                        .threshold(cartDiscountDetails.getThreshold())
                        .discount(cartDiscountDetails.getDiscount())
                        .build()
                ).build();

    }
}
