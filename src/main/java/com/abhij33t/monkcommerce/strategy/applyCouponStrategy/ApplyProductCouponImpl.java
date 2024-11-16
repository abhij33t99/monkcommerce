package com.abhij33t.monkcommerce.strategy.applyCouponStrategy;

import com.abhij33t.monkcommerce.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
public class ApplyProductCouponImpl implements ApplyCouponStrategy {

    private final ObjectMapper mapper;

    @Override
    public CartWithDiscountDto applyCoupon(CartDto cart, JsonNode baseDetails, Integer couponId) throws JsonProcessingException {
        var cartTotal = cart.getProductDetails().stream()
                .map(p -> p.getQuantity() * p.getPrice())
                .reduce(0.0, Double::sum);

        ProductWiseDetails productWiseDetails = mapper.treeToValue(baseDetails, ProductWiseDetails.class);
        CartWithDiscountDto cartWithDiscountDto = new CartWithDiscountDto();
        cartWithDiscountDto.setProductDetails(new ArrayList<>());

        AtomicReference<Double> totalDiscount = new AtomicReference<>(0d);
        cart.getProductDetails().stream()
                .map(p -> {
                    if (p.getProductId().equals(productWiseDetails.getProductId())) {
                        totalDiscount.set(p.getPrice() * productWiseDetails.getDiscount() / 100);
                        return new CartProductWithDiscountDetails(p.getProductId(), p.getQuantity(), p.getPrice(), p.getPrice() * productWiseDetails.getDiscount() / 100);
                    } else {
                        return new CartProductWithDiscountDetails(p.getProductId(), p.getQuantity(), p.getPrice(), 0.0);
                    }
                })
                .forEach(cartWithDiscountDto.getProductDetails()::add);

        cartWithDiscountDto.setTotalDiscount(totalDiscount.get());
        cartWithDiscountDto.setTotalPrice(cartTotal);
        cartWithDiscountDto.setFinalPrice(cartTotal - totalDiscount.get());
        return cartWithDiscountDto;
    }
}
