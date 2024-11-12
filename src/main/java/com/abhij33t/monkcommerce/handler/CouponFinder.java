package com.abhij33t.monkcommerce.handler;

import java.util.List;

import com.abhij33t.monkcommerce.dto.CartDto;
import com.abhij33t.monkcommerce.dto.DiscountDto;

public interface CouponFinder {
    void setNext(CouponFinder finder);
    void fetchApplicableCoupons(CartDto cart, List<DiscountDto> applicableCoupons);
}
