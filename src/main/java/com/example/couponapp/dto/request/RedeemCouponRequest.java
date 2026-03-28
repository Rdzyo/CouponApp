package com.example.couponapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RedeemCouponRequest(
        @NotNull
        Long customerId,
        @NotNull
        @NotBlank
        String couponName,
        @NotNull
        @NotBlank
        String ipAddress ) {
}
