package com.abhij33t.monkcommerce.strategy.addCouponStrategy;

import com.abhij33t.monkcommerce.model.Coupon;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface AddCouponStrategy {
    void addCouponDetails(JsonNode details, Coupon coupon) throws JsonProcessingException;
    void updateCouponDetails(JsonNode details, Coupon coupon) throws JsonProcessingException;
    void deleteCoupon(Coupon coupon);
}
