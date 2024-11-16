package com.abhij33t.monkcommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "create")
@NoArgsConstructor
public class CartDto {
    private List<CartProductDetails> productDetails;
}
