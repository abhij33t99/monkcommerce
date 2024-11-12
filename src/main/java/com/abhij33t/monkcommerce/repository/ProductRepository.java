package com.abhij33t.monkcommerce.repository;

import com.abhij33t.monkcommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
