package com.abhij33t.monkcommerce.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class CartWithDiscountDto {
    private List<CartProductWithDiscountDetails> productDetails;
    private Double totalPrice;
    private Double totalDiscount;
    private Double finalPrice;
}
