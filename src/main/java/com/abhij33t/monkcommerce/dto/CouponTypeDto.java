package com.abhij33t.monkcommerce.dto;

public enum CouponTypeDto {
    CART ("cart-wise"),
    PRODUCT ("product-wise"),
    BUY_GET ("bxgy");

    private String type;

    private CouponTypeDto(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
