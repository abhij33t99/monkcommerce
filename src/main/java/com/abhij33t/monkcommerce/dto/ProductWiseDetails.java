package com.abhij33t.monkcommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductWiseDetails extends BaseDetails {
    private Integer productId;
    private Integer discount;
}
