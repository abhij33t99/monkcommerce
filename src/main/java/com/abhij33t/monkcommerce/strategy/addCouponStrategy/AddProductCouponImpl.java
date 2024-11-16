package com.abhij33t.monkcommerce.strategy.addCouponStrategy;

import com.abhij33t.monkcommerce.dto.Field;
import com.abhij33t.monkcommerce.dto.ProductWiseDetails;
import com.abhij33t.monkcommerce.exception.NotFoundException;
import com.abhij33t.monkcommerce.model.Coupon;
import com.abhij33t.monkcommerce.model.ProductDiscountDetails;
import com.abhij33t.monkcommerce.repository.CouponRepository;
import com.abhij33t.monkcommerce.repository.ProductDiscountDetailsRepository;
import com.abhij33t.monkcommerce.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddProductCouponImpl implements AddCouponStrategy {

    private final ProductDiscountDetailsRepository productDiscountDetailsRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final ObjectMapper mapper;

    @Override
    public void addCouponDetails(JsonNode details, Coupon coupon) throws JsonProcessingException {
        var productDetails = mapper.treeToValue(details, ProductWiseDetails.class);
        var product = productRepository.findById(productDetails.getProductId())
                .orElseThrow(() -> new NotFoundException(Field.PRODUCT, productDetails.getProductId()));
        // insert into product discount table
        var productDiscountDetails = ProductDiscountDetails.builder()
                .coupon(coupon)
                .product(product)
                .discount(productDetails.getDiscount())
                .build();
        productDiscountDetailsRepository.save(productDiscountDetails);
    }

    @Override
    public void updateCouponDetails(JsonNode details, Coupon coupon) throws JsonProcessingException {
        var productDetails = mapper.treeToValue(details, ProductWiseDetails.class);
        var productDiscountDetails = productDiscountDetailsRepository.findByCoupon(coupon)
                .orElseThrow(() -> new NotFoundException(Field.COUPON_DETAILS, coupon.getId()));
        // update the fields
        productDiscountDetails.setDiscount(productDetails.getDiscount());
        productDiscountDetailsRepository.save(productDiscountDetails);
    }

    @Override
    public void deleteCoupon(Coupon coupon) {
        productDiscountDetailsRepository.deleteByCoupon(coupon);
        couponRepository.delete(coupon);
    }
}
