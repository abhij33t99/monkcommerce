package com.abhij33t.monkcommerce.handler;

import java.util.List;

import org.springframework.stereotype.Component;

import com.abhij33t.monkcommerce.dto.CartDto;
import com.abhij33t.monkcommerce.dto.DiscountDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BuyXGetYCouponFinder implements CouponFinder{

    private CouponFinder next;

    @Override
    public void setNext(CouponFinder finder) {
        this.next = finder;
    }

    @Override
    public void fetchApplicableCoupons(CartDto cart, List<DiscountDto> applicableCoupons) {
        
    }
    
}
