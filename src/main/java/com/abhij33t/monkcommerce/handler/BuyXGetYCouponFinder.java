package com.abhij33t.monkcommerce.handler;

import com.abhij33t.monkcommerce.dto.CartDto;
import com.abhij33t.monkcommerce.dto.CouponTypeDto;
import com.abhij33t.monkcommerce.dto.DiscountDto;
import com.abhij33t.monkcommerce.repository.BuyXGetYProductMappingRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuyXGetYCouponFinder implements CouponFinder{

    private CouponFinder next;
    private final ObjectMapper mapper;
    private final BuyXGetYProductMappingRepository buyXGetYProductMappingRepository;

    @Override
    public void setNext(CouponFinder finder) {
        this.next = finder;
    }

    @Override
    public void fetchApplicableCoupons(CartDto cart, List<DiscountDto> applicableCoupons) {
        try{
            var items = mapper.writeValueAsString(cart.getProductDetails());
            log.warn(items);
            buyXGetYProductMappingRepository.getApplicableBuyXGetYCoupon(items)
                    .stream()
                    .map(m -> DiscountDto.builder()
                            .couponId((Integer) m.get("v_coupon_id"))
                            .type(CouponTypeDto.BUY_GET.getType())
                            .discount(
                                    cart.getProductDetails().stream()
                                            .filter(pd -> Objects.equals(pd.getProductId(), (Integer) m.get("v_get_product_id")))
                                            .findFirst().get().getPrice() * (Integer) m.get("v_applicable_count")
                            )
                            .build()
                    )
                    .forEach(applicableCoupons::add);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            if (next != null) {
                next.fetchApplicableCoupons(cart, applicableCoupons);
            }
        }
    }
    
}
