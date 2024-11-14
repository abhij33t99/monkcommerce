package com.abhij33t.monkcommerce.strategy.applyCouponStrategy;

import com.abhij33t.monkcommerce.dto.CartDto;
import com.abhij33t.monkcommerce.model.Coupon;

public interface ApplyCouponStrategy {
    void applyCoupon(CartDto cart, Coupon coupon);
}
