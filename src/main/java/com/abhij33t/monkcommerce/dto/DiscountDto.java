package com.abhij33t.monkcommerce.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiscountDto {
    private Integer couponId;
    private String type;
    private Double discount;
}
