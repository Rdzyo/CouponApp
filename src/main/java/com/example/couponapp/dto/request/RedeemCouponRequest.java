package com.example.couponapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import static com.example.couponapp.util.message.JakartaValidationMessagesUtil.VALUE_NOT_BLANK;
import static com.example.couponapp.util.message.JakartaValidationMessagesUtil.VALUE_NOT_NULL;

public record RedeemCouponRequest(
        @NotNull(message = VALUE_NOT_NULL)
        Long customerId,
        @NotNull(message = VALUE_NOT_NULL)
        @NotBlank(message = VALUE_NOT_BLANK)
        String couponName,
        @NotNull(message = VALUE_NOT_NULL)
        @NotBlank(message = VALUE_NOT_BLANK)
        @Pattern(regexp = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$", message = "Must be valid IPv4 address")
        String ipAddress ) {
}
