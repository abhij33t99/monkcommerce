package com.abhij33t.monkcommerce.controller;

import com.abhij33t.monkcommerce.dto.CartDto;
import com.abhij33t.monkcommerce.dto.CartWithDiscountDto;
import com.abhij33t.monkcommerce.dto.CouponDto;
import com.abhij33t.monkcommerce.model.Coupon;
import com.abhij33t.monkcommerce.service.CouponService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/coupons")
    public ResponseEntity<Coupon> addCoupon(@RequestBody CouponDto couponRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.addCoupon(couponRequest));
    }

    @GetMapping("/coupons")
    public ResponseEntity<List<CouponDto>> getCoupons() {
        return ResponseEntity.ok(couponService.getCoupons());
    }

    @GetMapping("/coupons/{id}")
    public ResponseEntity<CouponDto> getCoupon(@PathVariable Integer id) {
        return ResponseEntity.ok(couponService.getCoupon(id));
    }

    @PutMapping("/coupons/{id}")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable Integer id, @RequestBody CouponDto couponRequest) {
        return ResponseEntity.ok(couponService.updateCoupon(id, couponRequest));
    }

    @DeleteMapping("/coupons/{id}")
    public ResponseEntity<CouponDto> deleteCoupon(@PathVariable Integer id) {
        return ResponseEntity.ok(couponService.deleteCoupon(id));
    }

    @PostMapping("/applicable-coupons")
    public ResponseEntity<List<CouponDto>> getApplicableCoupons(@RequestBody CartDto cart) {
        return ResponseEntity.ok(couponService.getApplicableCoupons(cart));
    }

    @PostMapping("/apply-coupon/{id}")
    public ResponseEntity<CartWithDiscountDto> applyCoupon(@PathVariable Integer id, @RequestBody CartDto cart) {
        return ResponseEntity.ok(couponService.applyCoupon(id, cart));
    }

}
