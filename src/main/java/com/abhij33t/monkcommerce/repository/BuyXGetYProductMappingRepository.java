package com.abhij33t.monkcommerce.repository;

import com.abhij33t.monkcommerce.model.BuyXGetYDetails;
import com.abhij33t.monkcommerce.model.BuyXGetYProductMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuyXGetYProductMappingRepository extends JpaRepository<BuyXGetYProductMapping, Integer> {
    List<BuyXGetYProductMapping> findAllByBuyXGetYDetails(BuyXGetYDetails buyGetDiscountDetails);

    void deleteAllByBuyXGetYDetails(BuyXGetYDetails buyXGetY);
}
