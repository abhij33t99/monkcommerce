package com.abhij33t.monkcommerce.strategy.applyCouponStrategy;

import com.abhij33t.monkcommerce.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class ApplyCartCouponImpl implements ApplyCouponStrategy {

    private final ObjectMapper mapper;

    @Override
    public CartWithDiscountDto applyCoupon(CartDto cart, JsonNode baseDetails, Integer couponId) throws JsonProcessingException {
        var cartTotal = cart.getProductDetails().stream()
                .map(p -> p.getQuantity() * p.getPrice())
                .reduce(0.0, Double::sum);

        CartWithDiscountDto cartWithDiscountDto = new CartWithDiscountDto();
        cartWithDiscountDto.setProductDetails(new ArrayList<>());

        cart.getProductDetails().stream()
                .map(p -> new CartProductWithDiscountDetails(p.getProductId(), p.getQuantity(), p.getPrice(),0.0))
                .forEach(cartWithDiscountDto.getProductDetails()::add);
        CartWiseDetails cartWiseDetails = mapper.treeToValue(baseDetails, CartWiseDetails.class);
        cartWithDiscountDto.setTotalDiscount(cartWiseDetails.getDiscount() * cartTotal / 100);
        cartWithDiscountDto.setTotalPrice(cartTotal);
        cartWithDiscountDto.setFinalPrice(cartTotal - cartWithDiscountDto.getTotalDiscount());
        return cartWithDiscountDto;
    }
}
