package com.abhij33t.monkcommerce.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Builder
public class BxGyDetails extends BaseDetails{
    private List<ProductDetails> buyProductDetails;
    private List<ProductDetails> getProductDetails;
    private Integer repetitionLimit;
}


