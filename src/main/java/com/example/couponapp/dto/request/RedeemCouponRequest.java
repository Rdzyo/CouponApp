package com.example.couponapp.dto.request;

public record RedeemCouponRequest(Long userId, String couponName, String ipAddress ) {
}
