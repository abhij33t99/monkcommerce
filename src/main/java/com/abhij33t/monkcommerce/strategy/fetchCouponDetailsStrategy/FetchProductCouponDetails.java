package com.abhij33t.monkcommerce.strategy.fetchCouponDetailsStrategy;

import com.abhij33t.monkcommerce.dto.CouponDto;
import com.abhij33t.monkcommerce.dto.CouponTypeDto;
import com.abhij33t.monkcommerce.dto.ProductWiseDetails;
import com.abhij33t.monkcommerce.model.Coupon;
import com.abhij33t.monkcommerce.repository.ProductDiscountDetailsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FetchProductCouponDetails implements FetchCouponDetailsStrategy {

    private final ProductDiscountDetailsRepository productDiscountDetailsRepository;
    private final ObjectMapper mapper;

    @Override
    public CouponDto fetchCouponDetails(Coupon coupon) {
        var productDiscountDetails = productDiscountDetailsRepository.findByCoupon(coupon).get();
        return CouponDto.builder()
                .type(CouponTypeDto.PRODUCT)
                .details(mapper.valueToTree(ProductWiseDetails.builder()
                        .productId(productDiscountDetails.getId())
                        .discount(productDiscountDetails.getDiscount())
                        .build())
                ).build();
    }
}
