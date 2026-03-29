package com.example.couponapp.controller;

import com.example.couponapp.dto.request.CreateCouponRequest;
import com.example.couponapp.dto.request.RedeemCouponRequest;
import com.example.couponapp.entity.Customer;
import com.example.couponapp.repository.CouponRepository;
import com.example.couponapp.repository.CustomerRepository;
import com.example.couponapp.util.message.CouponMessagesUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RedeemCouponTestIT extends BaseITTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    CustomerRepository customerRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
        couponRepository.deleteAll();
    }

    @Test
    void shouldRedeemCouponSuccessfully() throws Exception {
        //given
        var customer = new Customer();
        customer.setFullName("Test Guy");
        customer = customerRepository.save(customer);
        var couponName = "test";
        var maxUsage = 1;
        var country = "PL";
        var customerId = customer.getId();
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
        var customer = new Customer();
        customer.setFullName("Test Guy");
        customer = customerRepository.save(customer);
        var couponName = "test";
        var customerId = customer.getId();
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
        var customer = new Customer();
        customer.setFullName("Test Guy");
        customer = customerRepository.save(customer);
        var couponName = "test";
        var maxUsage = 1;
        var country = "US";
        var customerId = customer.getId();
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
        var customer = new Customer();
        customer.setFullName("Test Guy");
        customer = customerRepository.save(customer);
        var couponName = "test";
        var maxUsage = 1;
        var country = "PL";
        var customerId = customer.getId();
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

    @ParameterizedTest
    @CsvSource(value = {
            "NIL, 1, 192.0.0.1",
            "1, '', 192.0.0.1",
            "1, NIL, 192.0.0.1",
            "1, test, NIL",
            "1, test, ''",
            "1, test, wrongIpv4Pattern"
    }, nullValues = "NIL"
    )
    void redeemCouponShouldReturnValidationErrorWithGivenValues(Long customerId, String couponName, String ipAddress) throws Exception {
        //given
        var request = new RedeemCouponRequest(customerId, couponName, ipAddress);

        //expect
        mockMvc.perform(post("/redeemCoupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
