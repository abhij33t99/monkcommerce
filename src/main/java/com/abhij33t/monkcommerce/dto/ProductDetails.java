package com.abhij33t.monkcommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ProductDetails {
    private Integer productId;
    private Integer quantity;
}