package com.example.couponapp.controller;

import com.example.couponapp.dto.request.CreateCouponRequest;
import com.example.couponapp.dto.request.RedeemCouponRequest;
import com.example.couponapp.dto.response.CreateCouponResponse;
import com.example.couponapp.dto.response.RedeemCouponResponse;
import com.example.couponapp.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping(value = "/redeemCoupon", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RedeemCouponResponse> redeemCoupon(@RequestBody @Valid RedeemCouponRequest redeemCouponRequest) {
        return couponService.redeemCoupon(redeemCouponRequest);
    }

    @PostMapping(value = "/createCoupon", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateCouponResponse> createCoupon(@RequestBody @Valid CreateCouponRequest createCouponRequest) {
        return couponService.createCoupon(createCouponRequest);
    }
}
