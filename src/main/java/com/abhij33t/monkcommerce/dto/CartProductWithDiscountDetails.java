package com.abhij33t.monkcommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartProductWithDiscountDetails extends CartProductDetails {
    Double totalDiscount;

    public CartProductWithDiscountDetails(Integer productId, Integer quantity, Double price, Double totalDiscount) {
        super(productId, quantity, price);
        this.totalDiscount = totalDiscount;
    }
}
