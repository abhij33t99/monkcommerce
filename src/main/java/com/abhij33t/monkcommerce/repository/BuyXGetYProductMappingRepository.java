package com.abhij33t.monkcommerce.repository;

import com.abhij33t.monkcommerce.model.BuyXGetYDetails;
import com.abhij33t.monkcommerce.model.BuyXGetYProductMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface BuyXGetYProductMappingRepository extends JpaRepository<BuyXGetYProductMapping, Integer> {
    List<BuyXGetYProductMapping> findAllByBuyXGetYDetails(BuyXGetYDetails buyGetDiscountDetails);

    void deleteAllByBuyXGetYDetails(BuyXGetYDetails buyXGetY);

    @Query(value = "select * from get_bxgy_coupons(CAST(:items as JSON))", nativeQuery = true)
    List<Map<String, Object>> getApplicableBuyXGetYCoupon(@Param("items") String items);

    @Query(value = "select * from apply_bxgy_coupons(CAST(:items as JSON), CAST(:p_coupon_id as INTEGER))", nativeQuery = true)
    Map<String, Object> applyBuyXGetYCoupon(@Param("items") String items, @Param("p_coupon_id") Integer couponId);
}
