package com.abhij33t.monkcommerce.strategy.addCouponStrategy;

import com.abhij33t.monkcommerce.dto.BaseDetails;
import com.abhij33t.monkcommerce.dto.Field;
import com.abhij33t.monkcommerce.dto.ProductWiseDetails;
import com.abhij33t.monkcommerce.exception.NotFoundException;
import com.abhij33t.monkcommerce.model.Coupon;
import com.abhij33t.monkcommerce.model.ProductDiscountDetails;
import com.abhij33t.monkcommerce.repository.ProductDiscountDetailsRepository;
import com.abhij33t.monkcommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddProductCouponImpl implements AddCouponStrategy {

    private final ProductDiscountDetailsRepository productDiscountDetailsRepository;
    private final ProductRepository productRepository;

    @Override
    public void addCouponDetails(BaseDetails details, Coupon coupon) {
        var productDetails = (ProductWiseDetails) details;
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
    public void updateCouponDetails(BaseDetails details, Coupon coupon) {
        var productDetails = (ProductWiseDetails) details;
        var productDiscountDetails = productDiscountDetailsRepository.findByCoupon(coupon)
                .orElseThrow(() -> new NotFoundException(Field.COUPON_DETAILS, coupon.getId()));
        // update the fields
        productDiscountDetails.setDiscount(productDetails.getDiscount());
        productDiscountDetailsRepository.save(productDiscountDetails);
    }
}
