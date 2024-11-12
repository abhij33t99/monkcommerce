package com.abhij33t.monkcommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "create")
public class CartDto {
    private List<CartProductDetails> productDetails;
}