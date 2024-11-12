package com.abhij33t.monkcommerce.dto;

import lombok.Data;

@Data
public class CartProductDetails extends ProductDetails{
    private Double price;

    public CartProductDetails(Integer productId, Integer quantity, Double price) {
        super(productId, quantity);
        this.price = price;
    }
}
