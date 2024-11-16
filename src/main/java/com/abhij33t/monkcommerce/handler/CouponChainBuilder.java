package com.abhij33t.monkcommerce.handler;

import com.abhij33t.monkcommerce.dto.CouponTypeDto;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
@RequiredArgsConstructor
@Getter
public class CouponChainBuilder {

    private final CreateCouponFinder createCouponFinder;
    private CouponFinder head;

    @PostConstruct
    public void init() {
        head = buildCouponChain();
    }

    public CouponFinder buildCouponChain() {
        CouponFinder head = null;
        CouponFinder current = null;

        var set = EnumSet.allOf(CouponTypeDto.class);
        for(var e : set) {
            var finder = createCouponFinder.createCouponFinder(e);
            if (head == null) {
                head = finder;
            } else {
                current.setNext(finder);
            }
            current = finder;
        }

        return head;
    }
}
