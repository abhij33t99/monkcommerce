package com.abhij33t.monkcommerce.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
public class BuyXGetYDetails {
    @Id
    private Integer id;
    @OneToOne(cascade = CascadeType.ALL)
    private Coupon coupon;
    private Integer buy_quantity;
    private Integer get_quantity;
    private Integer repetition_limit;
}