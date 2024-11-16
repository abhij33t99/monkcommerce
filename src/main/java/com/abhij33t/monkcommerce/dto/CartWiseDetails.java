package com.abhij33t.monkcommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartWiseDetails extends BaseDetails {
    private Double threshold;
    private Integer discount;
}
