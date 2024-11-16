package com.abhij33t.monkcommerce.strategy.applyCouponStrategy;

import com.abhij33t.monkcommerce.dto.CartDto;
import com.abhij33t.monkcommerce.dto.CartProductWithDiscountDetails;
import com.abhij33t.monkcommerce.dto.CartWithDiscountDto;
import com.abhij33t.monkcommerce.repository.BuyXGetYProductMappingRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplyBxGyCouponImpl implements ApplyCouponStrategy {

    private final BuyXGetYProductMappingRepository buyXGetYProductMappingRepository;
    private final ObjectMapper mapper;

    @Override
    public CartWithDiscountDto applyCoupon(CartDto cart, JsonNode baseDetails, Integer couponId) throws JsonProcessingException {
        var cartTotal = cart.getProductDetails().stream()
                .map(p -> p.getQuantity() * p.getPrice())
                .reduce(0.0, Double::sum);

        CartWithDiscountDto cartWithDiscountDto = new CartWithDiscountDto();
        cartWithDiscountDto.setProductDetails(new ArrayList<>());
        AtomicReference<Double> totalDiscount = new AtomicReference<>(0d);
        var items = mapper.writeValueAsString(cart.getProductDetails());
        var res = buyXGetYProductMappingRepository.applyBuyXGetYCoupon(items, couponId);
        var result = (Integer[]) res.get("apply_bxgy_coupons");
        var productId = result[0];
        var applicableCount = result[1];
        cart.getProductDetails().stream()
                .map(p -> {
                    if (p.getProductId().equals(productId)) {
                        totalDiscount.set(p.getPrice() * applicableCount);
                        return new CartProductWithDiscountDetails(p.getProductId(), p.getQuantity(), p.getPrice(), p.getPrice() * applicableCount);
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
