package com.example.couponapp.controller;

import com.example.couponapp.dto.request.CreateCouponRequest;
import com.example.couponapp.entity.Coupon;
import com.example.couponapp.repository.CouponRepository;
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

import static com.example.couponapp.util.message.CouponMessagesUtil.COUPON_ALREADY_EXIST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CreateCouponTestIT extends BaseITTest {


    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    CouponRepository couponRepository;

    @BeforeEach
    void setUp() {
        couponRepository.deleteAll();
    }

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

    @Test
    void shouldReturn409IfCouponAlreadyExists() throws Exception {
        //given
        var couponName = "test";
        var maxUsage = 1;
        var country = "PL";
        var request = new CreateCouponRequest(couponName, maxUsage, country);
        var coupon = new Coupon();
        coupon.setCouponName(couponName);
        coupon.setMaxUsage(maxUsage);
        coupon.setCountry(country);
        couponRepository.save(coupon);

        //expect
        mockMvc.perform(post("/createCoupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage").value(COUPON_ALREADY_EXIST));
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
    void createCouponShouldReturnValidationErrorWithGivenValues(String couponName, Integer maxUsage, String country) throws Exception {
        //given
        var request = new CreateCouponRequest(couponName, maxUsage, country);

        //expect
        mockMvc.perform(post("/createCoupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
