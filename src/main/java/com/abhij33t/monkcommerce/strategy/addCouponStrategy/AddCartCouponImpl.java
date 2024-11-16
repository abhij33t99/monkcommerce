package com.abhij33t.monkcommerce.strategy.addCouponStrategy;

import com.abhij33t.monkcommerce.dto.CartWiseDetails;
import com.abhij33t.monkcommerce.dto.Field;
import com.abhij33t.monkcommerce.exception.NotFoundException;
import com.abhij33t.monkcommerce.model.CartDiscountDetails;
import com.abhij33t.monkcommerce.model.Coupon;
import com.abhij33t.monkcommerce.repository.CartDiscountDetailsRepository;
import com.abhij33t.monkcommerce.repository.CouponRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddCartCouponImpl implements AddCouponStrategy {

    private final CartDiscountDetailsRepository cartDiscountDetailsRepository;
    private final CouponRepository couponRepository;
    private final ObjectMapper mapper;

    @Override
    public void addCouponDetails(JsonNode details, Coupon coupon) throws JsonProcessingException {
        var cartDetails = mapper.treeToValue(details, CartWiseDetails.class);
        // insert into cart discount table
        var cartDiscountDetails = CartDiscountDetails.builder()
                .coupon(coupon)
                .threshold(cartDetails.getThreshold())
                .discount(cartDetails.getDiscount())
                .build();
        cartDiscountDetailsRepository.save(cartDiscountDetails);
    }

    @Override
    public void updateCouponDetails(JsonNode details, Coupon coupon) throws JsonProcessingException {
        var cartDetails = mapper.treeToValue(details, CartWiseDetails.class);
        var cartDiscountDetails = cartDiscountDetailsRepository.findByCoupon(coupon)
                .orElseThrow(() -> new NotFoundException(Field.COUPON_DETAILS, coupon.getId()));
        
        // update the fields
        cartDiscountDetails.setThreshold(cartDetails.getThreshold());
        cartDiscountDetails.setDiscount(cartDetails.getDiscount());
        cartDiscountDetailsRepository.save(cartDiscountDetails);
    }

    @Override
    public void deleteCoupon(Coupon coupon) {
        cartDiscountDetailsRepository.deleteByCoupon(coupon);
        couponRepository.delete(coupon);
    }
}
