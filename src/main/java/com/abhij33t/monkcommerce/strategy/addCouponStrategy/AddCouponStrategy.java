package com.abhij33t.monkcommerce.strategy.addCouponStrategy;

import com.abhij33t.monkcommerce.dto.BaseDetails;
import com.abhij33t.monkcommerce.model.Coupon;

public interface AddCouponStrategy {
    void addCouponDetails(BaseDetails details, Coupon coupon);
    void updateCouponDetails(BaseDetails details, Coupon coupon);
}
