package com.abhij33t.monkcommerce.handler;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.abhij33t.monkcommerce.dto.CartDto;
import com.abhij33t.monkcommerce.dto.CouponTypeDto;
import com.abhij33t.monkcommerce.dto.DiscountDto;
import com.abhij33t.monkcommerce.dto.Field;
import com.abhij33t.monkcommerce.exception.NotFoundException;
import com.abhij33t.monkcommerce.repository.ProductDiscountDetailsRepository;
import com.abhij33t.monkcommerce.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductCouponFinder implements CouponFinder {

    private CouponFinder next;
    private final ProductDiscountDetailsRepository productDiscountDetailsRepository;
    private final ProductRepository productRepository;

    @Override
    public void setNext(CouponFinder finder) {
        this.next = finder;
    }

    @Override
    public void fetchApplicableCoupons(CartDto cart, List<DiscountDto> applicableCoupons) {

        for (var pd : cart.getProductDetails()) {
            var product = productRepository.findById(pd.getProductId())
                    .orElseThrow(() -> new NotFoundException(Field.PRODUCT, pd.getProductId()));

            var productDiscountDetails = productDiscountDetailsRepository.findByProduct(product);
            if (productDiscountDetails.isPresent()) {
                applicableCoupons.add(DiscountDto.builder()
                        .couponId(productDiscountDetails.get().getCoupon().getId())
                        .discount(productDiscountDetails.get().getDiscount() / 100 * pd.getPrice() * pd.getQuantity())
                        .type(CouponTypeDto.PRODUCT.getType())
                        .build());
            }
        }

        if (next != null) {
            next.fetchApplicableCoupons(cart, applicableCoupons);
        }
    }

}
