package com.example.couponapp.controller;

import com.example.couponapp.dto.request.CreateCouponRequest;
import com.example.couponapp.dto.request.RedeemCouponRequest;
import com.example.couponapp.dto.response.CreateCouponResponse;
import com.example.couponapp.dto.response.RedeemCouponResponse;
import com.example.couponapp.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Tag(name = "Redeem Coupon")
    @Operation(summary = "Customer redeems coupon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon redeemed successfully"),
            @ApiResponse(responseCode = "400", description = "Coupon does not exist"),
            @ApiResponse(responseCode = "400", description = "Coupon is not available for customer's country"),
            @ApiResponse(responseCode = "400", description = "Coupon has been fully used up"),
            @ApiResponse(responseCode = "400", description = "Customer already redeemed that coupon before")
    })
    @PostMapping(value = "/redeemCoupon", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RedeemCouponResponse> redeemCoupon(@RequestBody @Valid RedeemCouponRequest redeemCouponRequest) {
        return couponService.redeemCoupon(redeemCouponRequest);
    }

    @Tag(name = "Create Coupon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Coupon successfully created"),
            @ApiResponse(responseCode = "400", description = "Coupon with that name already exists")
    })
    @PostMapping(value = "/createCoupon", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateCouponResponse> createCoupon(@RequestBody @Valid CreateCouponRequest createCouponRequest) {
        return couponService.createCoupon(createCouponRequest);
    }
}
