package com.abhij33t.monkcommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BxGyDetails extends BaseDetails{
    private List<ProductDetails> buyProductDetails;
    private List<ProductDetails> getProductDetails;
    private Integer repetitionLimit;
}


