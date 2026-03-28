package com.example.couponapp.controller;

import com.example.couponapp.dto.request.CreateCouponRequest;
import com.example.couponapp.dto.request.RedeemCouponRequest;
import com.example.couponapp.util.CouponMessagesUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CouponControllerTestIT extends BaseITTest {


    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateCouponAndReturnNewUrl() throws Exception {
        //given
        var couponName = "test";
        var maxUsage = 1;
        var country = "PL";
        var request = new CreateCouponRequest(couponName, maxUsage, country);

        //expect
        mockMvc.perform(post("/createCoupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.couponName").value("test"))
                .andExpect(jsonPath("$.maxUsage").value(1))
                .andExpect(jsonPath("$.country").value("PL"));
    }

    @ParameterizedTest
    @CsvSource(value = {
            " , 1, PL",
            "NIL, 1, PL",
            "'', 1, PL",
            "t$st, 1, PL",
            "test, 0, PL",
            "test, 1, ''",
            "test, 1, NIL",
            "test, 1, POL"
    }, nullValues = "NIL"
    )
    void shouldReturnValidationErrorWithGivenValues(String couponName, Integer maxUsage, String country) throws Exception {
        //given
        var request = new CreateCouponRequest(couponName, maxUsage, country);

        //expect
        mockMvc.perform(post("/createCoupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRedeemCouponSuccessfully() throws Exception {
        //given
        var couponName = "test";
        var maxUsage = 1;
        var country = "PL";
        var customerId = 1L;
        var ipAddress = "217.119.64.172";
        var createCouponRequest = new CreateCouponRequest(couponName, maxUsage, country);
        var redeemCouponRequest = new RedeemCouponRequest(customerId, couponName, ipAddress);

        //when
        mockMvc.perform(post("/createCoupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCouponRequest)));

        //then
        mockMvc.perform(post("/redeemCoupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(redeemCouponRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successMessage").value(CouponMessagesUtil.COUPON_REDEEMED_SUCCESS));
    }

    @Test
    void shouldFailRedeemCouponIfCouponDoesNotExist() throws Exception {
        //given
        var couponName = "test";
        var customerId = 1L;
        var ipAddress = "217.119.64.172";
        var redeemCouponRequest = new RedeemCouponRequest(customerId, couponName, ipAddress);

        //expect
        mockMvc.perform(post("/redeemCoupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(redeemCouponRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(CouponMessagesUtil.COUPON_DOES_NOT_EXIST));
    }

    @Test
    void shouldFailRedeemCouponIfIsNotAvailableForCountry() throws Exception {
        //given
        var couponName = "test";
        var maxUsage = 1;
        var country = "US";
        var customerId = 1L;
        var ipAddress = "217.119.64.172";
        var createCouponRequest = new CreateCouponRequest(couponName, maxUsage, country);
        var redeemCouponRequest = new RedeemCouponRequest(customerId, couponName, ipAddress);

        //when
        mockMvc.perform(post("/createCoupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCouponRequest)));

        //then
        mockMvc.perform(post("/redeemCoupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(redeemCouponRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(CouponMessagesUtil.COUPON_IS_NOT_AVAILABLE_FOR_COUNTRY));
    }

    @Test
    void shouldFailRedeemCouponIfItHasBeenFullyUsed() throws Exception {
        //given
        var couponName = "test";
        var maxUsage = 1;
        var country = "PL";
        var customerId = 1L;
        var ipAddress = "217.119.64.172";
        var createCouponRequest = new CreateCouponRequest(couponName, maxUsage, country);
        var redeemCouponRequest = new RedeemCouponRequest(customerId, couponName, ipAddress);

        //when
        mockMvc.perform(post("/createCoupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCouponRequest)));

        mockMvc.perform(post("/redeemCoupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(redeemCouponRequest)));

        //then
        mockMvc.perform(post("/redeemCoupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(redeemCouponRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(CouponMessagesUtil.COUPON_MAX_USAGE_REACHED));
    }
}
