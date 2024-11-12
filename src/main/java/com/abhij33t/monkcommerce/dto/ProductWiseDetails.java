package com.abhij33t.monkcommerce.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductWiseDetails extends BaseDetails {
    private Integer productId;
    private Integer discount;
}
