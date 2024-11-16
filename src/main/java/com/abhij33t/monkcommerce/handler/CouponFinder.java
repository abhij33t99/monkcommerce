package com.abhij33t.monkcommerce.handler;

import com.abhij33t.monkcommerce.dto.CartDto;
import com.abhij33t.monkcommerce.dto.DiscountDto;

import java.util.List;

public interface CouponFinder {
    void setNext(CouponFinder finder);
    void fetchApplicableCoupons(CartDto cart, List<DiscountDto> applicableCoupons);
}
