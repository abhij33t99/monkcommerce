package com.abhij33t.monkcommerce.service;

import com.abhij33t.monkcommerce.dto.*;
import com.abhij33t.monkcommerce.exception.NotFoundException;
import com.abhij33t.monkcommerce.handler.CouponChainBuilder;
import com.abhij33t.monkcommerce.model.Coupon;
import com.abhij33t.monkcommerce.model.CouponType;
import com.abhij33t.monkcommerce.repository.CouponRepository;
import com.abhij33t.monkcommerce.repository.CouponTypeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponTypeRepository couponTypeRepository;
    private final CouponRepository couponRepository;
    private final CouponStrategyFactory couponStrategyFactory;
    private final FetchCouponDetailsStrategyFactory fetchCouponDetailsStrategyFactory;
    private final CouponChainBuilder couponChainBuilder;
    private final ApplyCouponStrategyFactory applyCouponStrategyFactory;
    private final ObjectMapper mapper;

    @Transactional
    public Coupon addCoupon(CouponDto couponRequest) {
        try {
            // insert base details for coupon
            Optional<CouponType> couponTypeOpt = couponTypeRepository.findByName(couponRequest.getType());
            CouponType couponType;
            couponType = couponTypeOpt.orElseGet(() -> couponTypeRepository
                    .save(CouponType.builder().name(couponRequest.getType()).build()));
            // create coupon
            Coupon coupon = Coupon.builder().couponType(couponType).expirationDate(couponRequest.getExpirationDate())
                    .build();
            // insert details for different related coupons
            couponStrategyFactory.getStrategy(couponRequest.getType()).addCouponDetails(couponRequest.getDetails(), coupon);
            return couponRepository.save(coupon);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<CouponDto> getCoupons() {
        // get all coupons
        List<Coupon> coupons = couponRepository.findAll();
        // fetch coupon details
        return coupons.stream()
                .map(c -> fetchCouponDetailsStrategyFactory
                        .getStrategy(c.getCouponType().getName()).fetchCouponDetails(c))
                .toList();
    }

    public CouponDto getCoupon(Integer id) {
        // get coupon
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Field.COUPON, id));
        // fetch coupon details
        return fetchCouponDetailsStrategyFactory.getStrategy(coupon.getCouponType().getName())
                .fetchCouponDetails(coupon);
    }

    @Transactional
    public CouponDto deleteCoupon(Integer id) {
        // get coupon
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Field.COUPON, id));
        // fetch coupon details
        var couponDetails = fetchCouponDetailsStrategyFactory
                .getStrategy(coupon.getCouponType().getName()).fetchCouponDetails(coupon);
        // delete coupon and its details from db
        couponStrategyFactory.getStrategy(coupon.getCouponType().getName()).deleteCoupon(coupon);
        return couponDetails;
    }

    public List<DiscountDto> getApplicableCoupons(CartDto cart) {
        List<DiscountDto> discountDtos = new ArrayList<>();
        couponChainBuilder.getHead().fetchApplicableCoupons(cart, discountDtos);
        return discountDtos;
    }

    public Coupon updateCoupon(Integer id, CouponDto couponRequest) {
        try {
            Coupon coupon = couponRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(Field.COUPON, id));
            // update coupon details
            couponStrategyFactory.getStrategy(couponRequest.getType()).updateCouponDetails(couponRequest.getDetails(),
                    coupon);
            return couponRepository.save(coupon);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public CartWithDiscountDto applyCoupon(Integer id, CartDto cart) {
        CouponDto couponDto = getCoupon(id);
        try {
            return applyCouponStrategyFactory.getStrategy(couponDto.getType()).applyCoupon(cart, couponDto.getDetails(), id);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
