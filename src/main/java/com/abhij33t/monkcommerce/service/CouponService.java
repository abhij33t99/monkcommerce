package com.abhij33t.monkcommerce.service;

import com.abhij33t.monkcommerce.dto.CartDto;
import com.abhij33t.monkcommerce.dto.CartWithDiscountDto;
import com.abhij33t.monkcommerce.dto.CouponDto;
import com.abhij33t.monkcommerce.dto.CouponTypeDto;
import com.abhij33t.monkcommerce.dto.Field;
import com.abhij33t.monkcommerce.exception.NotFoundException;
import com.abhij33t.monkcommerce.model.Coupon;
import com.abhij33t.monkcommerce.model.CouponType;
import com.abhij33t.monkcommerce.repository.CouponRepository;
import com.abhij33t.monkcommerce.repository.CouponTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponTypeRepository couponTypeRepository;
    private final CouponRepository couponRepository;
    private final CouponStrategyFactory couponStrategyFactory;
    private final FetchCouponDetailsStrategyFactory fetchCouponDetailsStrategyFactory;

    @Transactional
    public Coupon addCoupon(CouponDto couponRequest) {
        // insert base details for coupon
        CouponType couponType = couponTypeRepository.findByName(couponRequest.getType().getType())
                .orElse(
                        couponTypeRepository
                                .save(CouponType.builder().name(couponRequest.getType().getType()).build()));
        // create coupon
        Coupon coupon = Coupon.builder().couponType(couponType).expirationDate(couponRequest.getExpirationDate())
                .build();
        // insert details for different related coupons
        couponStrategyFactory.getStrategy(couponRequest.getType()).addCouponDetails(couponRequest.getDetails(), coupon);
        return couponRepository.save(coupon);

    }

    public List<CouponDto> getCoupons() {
        // get all coupons
        List<Coupon> coupons = couponRepository.findAll();
        // fetch coupon details
        return coupons.stream()
                .map(c -> fetchCouponDetailsStrategyFactory
                        .getStrategy(CouponTypeDto.valueOf(c.getCouponType().getName())).fetchCouponDetails(c))
                .toList();
    }

    public CouponDto getCoupon(Integer id) {
        // get coupon
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Field.COUPON, id));
        // fetch coupon details
        return fetchCouponDetailsStrategyFactory.getStrategy(CouponTypeDto.valueOf(coupon.getCouponType().getName()))
                .fetchCouponDetails(coupon);
    }

    @Transactional
    public CouponDto deleteCoupon(Integer id) {
        // get coupon
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Field.COUPON, id));
        // fetch coupon details
        var couponDetails = fetchCouponDetailsStrategyFactory
                .getStrategy(CouponTypeDto.valueOf(coupon.getCouponType().getName())).fetchCouponDetails(coupon);
        // delete coupon and its details from db
        couponRepository.delete(coupon);
        return couponDetails;
    }

    public List<CouponDto> getApplicableCoupons(CartDto cart) {
        return null;
    }

    public Coupon updateCoupon(Integer id, CouponDto couponRequest) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Field.COUPON, id));
        // update coupon details
        couponStrategyFactory.getStrategy(couponRequest.getType()).updateCouponDetails(couponRequest.getDetails(),
                coupon);
        return couponRepository.save(coupon);
    }

    public CartWithDiscountDto applyCoupon(Integer id, CartDto cart) {
        return null;
    }

}
