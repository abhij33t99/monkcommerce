package com.abhij33t.monkcommerce.dto;

import java.util.List;

import lombok.Data;

@Data
public class CartWithDiscountDto {
    private List<CartProductWithDiscountDetails> productDetails;
    private Double totalPrice;
    private Double totalDiscount;
    private Double finalPrice;
}
