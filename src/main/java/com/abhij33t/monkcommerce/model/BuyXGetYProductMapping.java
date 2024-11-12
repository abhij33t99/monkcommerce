package com.abhij33t.monkcommerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
public class BuyXGetYProductMapping {
    @Id
    private Integer id;
    @ManyToOne
    private BuyXGetYDetails buyXGetYDetails;
    private TransactionType transactionType;
    @ManyToOne
    private Product product;
}
