package com.abhij33t.monkcommerce.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponDto {
    private CouponTypeDto type;
    private LocalDate expirationDate;
    private JsonNode details;
}
