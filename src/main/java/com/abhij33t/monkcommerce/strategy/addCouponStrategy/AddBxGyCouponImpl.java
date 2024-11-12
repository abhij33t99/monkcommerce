package com.abhij33t.monkcommerce.strategy.addCouponStrategy;

import com.abhij33t.monkcommerce.dto.BaseDetails;
import com.abhij33t.monkcommerce.dto.BxGyDetails;
import com.abhij33t.monkcommerce.dto.Field;
import com.abhij33t.monkcommerce.exception.NotFoundException;
import com.abhij33t.monkcommerce.model.BuyXGetYDetails;
import com.abhij33t.monkcommerce.model.BuyXGetYProductMapping;
import com.abhij33t.monkcommerce.model.Coupon;
import com.abhij33t.monkcommerce.model.TransactionType;
import com.abhij33t.monkcommerce.repository.BuyXGetYDetailsRepository;
import com.abhij33t.monkcommerce.repository.BuyXGetYProductMappingRepository;
import com.abhij33t.monkcommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddBxGyCouponImpl implements AddCouponStrategy {

        private final BuyXGetYDetailsRepository buyGetDiscountDetailsRepository;
        private final BuyXGetYProductMappingRepository buyXGetYProductMappingRepository;
        private final ProductRepository productRepository;

        @Override
        public void addCouponDetails(BaseDetails details, Coupon coupon) {
                var bxGyDetails = (BxGyDetails) details;

                // insert into buy get discount table
                var buyGetDiscountDetails = buyGetDiscountDetailsRepository.save(BuyXGetYDetails.builder()
                                .coupon(coupon)
                                .buy_quantity(bxGyDetails.getBuyProductDetails().get(0).getQuantity())
                                .get_quantity(bxGyDetails.getGetProductDetails().get(0).getQuantity())
                                .repetition_limit(bxGyDetails.getRepetitionLimit())
                                .build());
                enrichDetails(bxGyDetails, buyGetDiscountDetails);
        }

        private void enrichDetails(BxGyDetails bxGyDetails, BuyXGetYDetails buyXGetY) {
                bxGyDetails.getBuyProductDetails()
                                .forEach(p -> {
                                        buyXGetYProductMappingRepository.save(
                                                        BuyXGetYProductMapping.builder()
                                                                        .buyXGetYDetails(buyXGetY)
                                                                        .product(productRepository
                                                                                        .findById(p.getProductId())
                                                                                        .get())
                                                                        .transactionType(TransactionType.BUY)
                                                                        .build());
                                });

                bxGyDetails.getGetProductDetails()
                                .forEach(p -> {
                                        buyXGetYProductMappingRepository.save(
                                                        BuyXGetYProductMapping.builder()
                                                                        .buyXGetYDetails(buyXGetY)
                                                                        .product(productRepository
                                                                                        .findById(p.getProductId())
                                                                                        .get())
                                                                        .transactionType(TransactionType.GET)
                                                                        .build());
                                });
        }

        @Override
        public void updateCouponDetails(BaseDetails details, Coupon coupon) {
                var bxGyDetails = (BxGyDetails) details;
                var buyGetDiscountDetails = buyGetDiscountDetailsRepository.findByCoupon(coupon)
                                .orElseThrow(() -> new NotFoundException(Field.COUPON_DETAILS, coupon.getId()));
                // update the fields
                buyGetDiscountDetails.setBuy_quantity(bxGyDetails.getBuyProductDetails().get(0).getQuantity());
                buyGetDiscountDetails.setGet_quantity(bxGyDetails.getGetProductDetails().get(0).getQuantity());
                buyGetDiscountDetails.setRepetition_limit(bxGyDetails.getRepetitionLimit());
                updateDetails(bxGyDetails, buyGetDiscountDetails);
        }

        private void updateDetails(BxGyDetails bxGyDetails, BuyXGetYDetails buyXGetY) {
                buyXGetYProductMappingRepository.deleteAllByBuyXGetYDetails(buyXGetY);
                enrichDetails(bxGyDetails, buyXGetY);
        }
}
