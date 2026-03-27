package com.example.couponapp.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCouponRequest(
        @NotNull(message = "Value must not be null")
        @NotBlank(message = "Value must not be empty")
        String couponName,
        @Min(value = 1, message = "Value must be more than zero")
        Integer maxUsage,
        @NotNull(message = "Value must not be null")
        @NotBlank(message = "Value must not be empty")
        String country) {
}
