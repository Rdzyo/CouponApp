package com.example.couponapp.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.example.couponapp.util.message.JakartaValidationMessagesUtil.VALUE_MORE_THAN_ZERO;
import static com.example.couponapp.util.message.JakartaValidationMessagesUtil.VALUE_NOT_BLANK;
import static com.example.couponapp.util.message.JakartaValidationMessagesUtil.VALUE_NOT_NULL;

public record CreateCouponRequest(
        @NotNull(message = VALUE_NOT_NULL)
        @NotBlank(message = VALUE_NOT_BLANK)
        @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Coupon name only allows letters and numbers")
        @Schema(description = "Name of the coupon", defaultValue = "couponName")
        String couponName,
        @Min(value = 1, message = VALUE_MORE_THAN_ZERO)
        Integer maxUsage,
        @NotNull(message = VALUE_NOT_NULL)
        @NotBlank(message = VALUE_NOT_BLANK)
        @Size(min = 2, max = 2, message = "Must be 2 letter ISO code")
        @Pattern(regexp = "[A-Z]+", message = "Country value should be ISO code of the country and must be capital letters")
        @Schema(description = "2-letter ISO Code of the country", defaultValue = "PL")
        String country) {
}
