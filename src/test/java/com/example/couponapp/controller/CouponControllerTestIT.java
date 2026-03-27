package com.example.couponapp.controller;

import com.example.couponapp.dto.request.CreateCouponRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CouponControllerTestIT {

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
            "test, 0, PL",
            "test, 1, ''",
            "test, 1, NIL"
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
}
