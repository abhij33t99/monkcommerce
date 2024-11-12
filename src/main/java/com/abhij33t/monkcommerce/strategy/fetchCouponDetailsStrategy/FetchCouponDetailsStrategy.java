package com.abhij33t.monkcommerce.strategy.fetchCouponDetailsStrategy;

import com.abhij33t.monkcommerce.dto.CouponDto;
import com.abhij33t.monkcommerce.model.Coupon;

public interface FetchCouponDetailsStrategy {
    CouponDto fetchCouponDetails(Coupon coupon);
}
