package com.abhij33t.monkcommerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Product {
    @Id
    private Integer id;
    private String name;
    private Double price;
}
