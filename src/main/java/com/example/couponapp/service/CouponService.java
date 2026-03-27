package com.example.couponapp.service;

import com.example.couponapp.dto.request.CreateCouponRequest;
import com.example.couponapp.dto.request.RedeemCouponRequest;
import com.example.couponapp.dto.response.CreateCouponResponse;
import org.springframework.http.ResponseEntity;

public interface CouponService {

    String redeemCoupon(RedeemCouponRequest redeemCouponRequest);
    ResponseEntity<CreateCouponResponse> createCoupon(CreateCouponRequest createCouponRequest);

}
