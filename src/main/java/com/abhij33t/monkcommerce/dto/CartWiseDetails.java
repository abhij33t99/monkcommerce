package com.abhij33t.monkcommerce.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartWiseDetails extends BaseDetails {
    private Double threshold;
    private Integer discount;
}
