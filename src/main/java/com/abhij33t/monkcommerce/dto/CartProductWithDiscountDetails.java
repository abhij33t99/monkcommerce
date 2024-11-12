package com.abhij33t.monkcommerce.dto;

import lombok.Data;

@Data
public class CartProductWithDiscountDetails extends CartProductDetails {
    Double totalDiscount;

    public CartProductWithDiscountDetails(Integer productId, Integer quantity, Double price, Double totalDiscount) {
        super(productId, quantity, price);
        this.totalDiscount = totalDiscount;
    }
}
