package com.abhij33t.monkcommerce.strategy.addCouponStrategy;

import com.abhij33t.monkcommerce.dto.BaseDetails;
import com.abhij33t.monkcommerce.dto.CartWiseDetails;
import com.abhij33t.monkcommerce.dto.Field;
import com.abhij33t.monkcommerce.exception.NotFoundException;
import com.abhij33t.monkcommerce.model.CartDiscountDetails;
import com.abhij33t.monkcommerce.model.Coupon;
import com.abhij33t.monkcommerce.repository.CartDiscountDetailsRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddCartCouponImpl implements AddCouponStrategy {

    private final CartDiscountDetailsRepository cartDiscountDetailsRepository;

    @Override
    public void addCouponDetails(BaseDetails details, Coupon coupon) {
        var cartDetails = (CartWiseDetails) details;

        // insert into cart discount table
        var cartDiscountDetails = CartDiscountDetails.builder()
                .coupon(coupon)
                .threshold(cartDetails.getThreshold())
                .discount(cartDetails.getDiscount())
                .build();
        cartDiscountDetailsRepository.save(cartDiscountDetails);
    }

    @Override
    public void updateCouponDetails(BaseDetails details, Coupon coupon) {
        var cartDetails = (CartWiseDetails) details;
        var cartDiscountDetails = cartDiscountDetailsRepository.findByCoupon(coupon)
                .orElseThrow(() -> new NotFoundException(Field.COUPON_DETAILS, coupon.getId()));
        
        // update the fields
        cartDiscountDetails.setThreshold(cartDetails.getThreshold());
        cartDiscountDetails.setDiscount(cartDetails.getDiscount());
        cartDiscountDetailsRepository.save(cartDiscountDetails);
    }
}
