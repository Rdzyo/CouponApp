package com.example.couponapp.util.validation;

import com.example.couponapp.dto.response.RedeemCouponResponse;
import com.example.couponapp.entity.Coupon;
import com.example.couponapp.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.example.couponapp.util.CouponMessagesUtil.COUPON_DOES_NOT_EXIST;
import static com.example.couponapp.util.CouponMessagesUtil.COUPON_IS_NOT_AVAILABLE_FOR_COUNTRY;
import static com.example.couponapp.util.CouponMessagesUtil.COUPON_MAX_USAGE_REACHED;

@Component
@RequiredArgsConstructor
public class CouponValidator {

    private final CouponRepository couponRepository;

    //Could be changed to collect all messages, but it is faster to return whichever is first
    public RedeemCouponResponse validateRedeemCouponRequest(String country, Optional<Coupon> couponOpt) {
        if (couponOpt.isEmpty()) {
            return RedeemCouponResponse.builder()
                    .errorMessage(COUPON_DOES_NOT_EXIST)
                    .build();
        } else if (!couponOpt.get().getCountry().contains(country)) {
            return RedeemCouponResponse.builder()
                    .errorMessage(COUPON_IS_NOT_AVAILABLE_FOR_COUNTRY)
                    .build();
        } else if (couponOpt.get().getMaxUsage() <= couponOpt.get().getCurrentUsage()) {
            return RedeemCouponResponse.builder()
                    .errorMessage(COUPON_MAX_USAGE_REACHED)
                    .build();
        }
        return RedeemCouponResponse.builder().build();
    }

    public boolean couponAlreadyExist(String couponName) {
        return couponRepository.existsByCouponNameIsLikeIgnoreCase(couponName);
    }
}
