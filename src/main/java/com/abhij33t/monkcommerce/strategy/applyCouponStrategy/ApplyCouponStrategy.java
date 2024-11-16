package com.abhij33t.monkcommerce.strategy.applyCouponStrategy;

import com.abhij33t.monkcommerce.dto.CartDto;
import com.abhij33t.monkcommerce.dto.CartWithDiscountDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface ApplyCouponStrategy {
    CartWithDiscountDto applyCoupon(CartDto cart, JsonNode baseDetails, Integer couponId) throws JsonProcessingException;
}
