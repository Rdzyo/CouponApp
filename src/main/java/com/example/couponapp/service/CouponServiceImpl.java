package com.example.couponapp.service;

import com.example.couponapp.dto.mapper.CouponMapper;
import com.example.couponapp.dto.request.CreateCouponRequest;
import com.example.couponapp.dto.request.RedeemCouponRequest;
import com.example.couponapp.dto.response.CreateCouponResponse;
import com.example.couponapp.entity.Coupon;
import com.example.couponapp.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    public String redeemCoupon(RedeemCouponRequest redeemCouponRequest) {
        return "";
    }

    @Override
    public ResponseEntity<CreateCouponResponse> createCoupon(CreateCouponRequest createCouponRequest) {
        var coupon = CouponMapper.INSTANCE.createRequestToEntity(createCouponRequest);
        if(!couponAlreadyExists(createCouponRequest.couponName(), createCouponRequest.country())) {
            coupon = couponRepository.save(coupon);
        } else {
            return ResponseEntity.badRequest()
                    .body(
                            CreateCouponResponse.builder()
                                    .errorMessage("Coupon with that name already exists")
                                    .build());
        }
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromPath("/createdCoupon/{id}")
                .buildAndExpand(coupon.getId())
                        .toUri())
                .body(buildCreateCouponResponse(coupon));
    }

    private CreateCouponResponse buildCreateCouponResponse(Coupon coupon) {
        return CreateCouponResponse.builder()
                .couponName(coupon.getCouponName())
                .maxUsage(coupon.getMaxUsage())
                .country(coupon.getCountry())
                .build();
    }

    private boolean couponAlreadyExists(String couponName, String country) {
       return couponRepository.existsByCouponNameIsLikeIgnoreCaseAndCountry(couponName, country);
    }
}
