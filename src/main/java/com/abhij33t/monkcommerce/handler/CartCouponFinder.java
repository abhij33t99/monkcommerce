package com.abhij33t.monkcommerce.handler;

import com.abhij33t.monkcommerce.dto.CartDto;
import com.abhij33t.monkcommerce.dto.CouponTypeDto;
import com.abhij33t.monkcommerce.dto.DiscountDto;
import com.abhij33t.monkcommerce.model.CartDiscountDetails;
import com.abhij33t.monkcommerce.repository.CartDiscountDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CartCouponFinder implements CouponFinder {

    private CouponFinder next;
    private final CartDiscountDetailsRepository cartDiscountDetailsRepository;

    @Override
    public void setNext(CouponFinder finder) {
        this.next = finder;
    }

    @Override
    public void fetchApplicableCoupons(CartDto cart, List<DiscountDto> applicableCoupons) {
        // fetch applicable cart coupons
        var cartTotal = cart.getProductDetails().stream()
                .map(p -> p.getQuantity() * p.getPrice())
                .reduce(0.0, Double::sum);

        List<CartDiscountDetails> cartDiscountDetails = cartDiscountDetailsRepository.findByThresholdLessThan(cartTotal);
        cartDiscountDetails.stream()
                .map(cd -> DiscountDto.builder()
                        .couponId(cd.getCoupon().getId())
                        .discount(cd.getDiscount() * cartTotal / 100)
                        .type(CouponTypeDto.CART.getType())
                        .build())
                .forEach(applicableCoupons::add);
        if (next != null) {
            next.fetchApplicableCoupons(cart, applicableCoupons);
        }
    }

}
