package com.abhij33t.monkcommerce.dto;

import lombok.Getter;

@Getter
public enum CouponTypeDto {
    CART ("cart-wise"),
    PRODUCT ("product-wise"),
    BUY_GET ("bxgy");

    private final String type;

    private CouponTypeDto(String type) {
        this.type = type;
    }
}
