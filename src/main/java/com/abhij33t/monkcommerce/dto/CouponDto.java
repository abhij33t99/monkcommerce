package com.abhij33t.monkcommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
public class CouponDto {
    private CouponTypeDto type;
    private LocalDate expirationDate;
    private BaseDetails details;
}
