package com.abhij33t.monkcommerce.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Builder
public class Coupon {
    private Integer id;
    @ManyToOne(cascade = CascadeType.ALL)
    private CouponType couponType;
    private LocalDate expirationDate;
}
