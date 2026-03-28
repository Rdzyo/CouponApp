package com.example.couponapp.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateCouponRequest(
        @NotNull(message = "Value must not be null")
        @NotBlank(message = "Value must not be empty")
        @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Coupon name only allows letters and numbers")
        @Schema(description = "Name of the coupon", defaultValue = "couponName")
        String couponName,
        @Min(value = 1, message = "Value must be more than zero")
        Integer maxUsage,
        @NotNull(message = "Value must not be null")
        @NotBlank(message = "Value must not be empty")
        @Max(value = 2, message = "Must be 2 letter ISO code")
        @Pattern(regexp = "^[A-Z]$", message = "Country field value must be capital letters")
        @Schema(description = "2-letter ISO Code of the country", defaultValue = "PL")
        String country) {
}
