package com.abhij33t.monkcommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartProductDetails extends ProductDetails{
    private Double price;

    public CartProductDetails(Integer productId, Integer quantity, Double price) {
        super(productId, quantity);
        this.price = price;
    }
}
