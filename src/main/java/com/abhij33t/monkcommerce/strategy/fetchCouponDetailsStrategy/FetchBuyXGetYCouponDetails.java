package com.abhij33t.monkcommerce.strategy.fetchCouponDetailsStrategy;

import com.abhij33t.monkcommerce.dto.BxGyDetails;
import com.abhij33t.monkcommerce.dto.CouponDto;
import com.abhij33t.monkcommerce.dto.CouponTypeDto;
import com.abhij33t.monkcommerce.dto.ProductDetails;
import com.abhij33t.monkcommerce.model.Coupon;
import com.abhij33t.monkcommerce.repository.BuyXGetYDetailsRepository;
import com.abhij33t.monkcommerce.repository.BuyXGetYProductMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FetchBuyXGetYCouponDetails implements FetchCouponDetailsStrategy {

    private final BuyXGetYDetailsRepository buyGetDiscountDetailsRepository;
    private final BuyXGetYProductMappingRepository buyXGetYProductMappingRepository;

    @Override
    public CouponDto fetchCouponDetails(Coupon coupon) {
        var buyGetDiscountDetails = buyGetDiscountDetailsRepository.findByCoupon(coupon).get();
        var productDetails = buyXGetYProductMappingRepository.findAllByBuyXGetYDetails(buyGetDiscountDetails);

        List<ProductDetails> buyProductDetails = new ArrayList<>();
        List<ProductDetails> getProductDetails = new ArrayList<>();
        for (var productDetail : productDetails) {
            switch (productDetail.getTransactionType()) {
                case BUY -> buyProductDetails
                        .add(new ProductDetails(productDetail.getProduct().getId(), buyGetDiscountDetails.getBuy_quantity()));
                case GET -> getProductDetails
                        .add(new ProductDetails(productDetail.getProduct().getId(), buyGetDiscountDetails.getGet_quantity()));
            }
        }

        return CouponDto.builder()
                .type(CouponTypeDto.BUY_GET)
                .details(
                        BxGyDetails.builder()
                                .buyProductDetails(buyProductDetails)
                                .getProductDetails(getProductDetails)
                                .repetitionLimit(buyGetDiscountDetails.getRepetition_limit())
                                .build())
                .build();
    }
}
