package com.example.couponapp.service;

import com.example.couponapp.dto.mapper.CouponMapper;
import com.example.couponapp.dto.request.CreateCouponRequest;
import com.example.couponapp.dto.request.RedeemCouponRequest;
import com.example.couponapp.dto.response.CreateCouponResponse;
import com.example.couponapp.dto.response.RedeemCouponResponse;
import com.example.couponapp.entity.Coupon;
import com.example.couponapp.entity.Customer;
import com.example.couponapp.repository.CouponRepository;
import com.example.couponapp.repository.CustomerRepository;
import com.example.couponapp.validation.CouponValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static com.example.couponapp.util.message.CouponMessagesUtil.COUPON_ALREADY_EXIST;
import static com.example.couponapp.util.message.CouponMessagesUtil.COUPON_REDEEMED_SUCCESS;
import static com.example.couponapp.util.GeolocationUtil.extractCountryFromIpAddress;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CustomerRepository customerRepository;
    private final CouponValidator couponValidator;

    @Override
    @Transactional
    public ResponseEntity<RedeemCouponResponse> redeemCoupon(RedeemCouponRequest redeemCouponRequest) {
        var country = extractCountryFromIpAddress(redeemCouponRequest.ipAddress());
        var couponName = redeemCouponRequest.couponName();
        var couponOpt = couponRepository.findByCouponNameIsIgnoreCase(couponName);
        var customerOpt = customerRepository.findById(redeemCouponRequest.customerId());
        var errorResponse = couponValidator.validateRedeemCouponRequest(country, couponOpt, customerOpt.get().getId());
        if(errorResponse.getErrorMessage() != null ) {
            return ResponseEntity.badRequest()
                    .body(errorResponse);
        } else {
            var coupon = couponOpt.get();
            var customer = customerOpt.get();
            incrementCouponUsage(couponName, country);
            saveCustomer(customer, coupon);
        }
        return ResponseEntity.ok().body(
                RedeemCouponResponse.builder()
                        .successMessage(COUPON_REDEEMED_SUCCESS)
                        .build()
        );
    }

    @Override
    public ResponseEntity<CreateCouponResponse> createCoupon(CreateCouponRequest createCouponRequest) {
        var coupon = CouponMapper.INSTANCE.createRequestToEntity(createCouponRequest);
        if(couponValidator.couponAlreadyExist(createCouponRequest.couponName())) {
            return ResponseEntity.badRequest()
                    .body(
                            CreateCouponResponse.builder()
                                    .errorMessage(COUPON_ALREADY_EXIST)
                                    .build());
        } else {
            coupon = couponRepository.save(coupon);
            return ResponseEntity.created(
                            ServletUriComponentsBuilder.fromPath("/createdCoupon/{id}")
                                    .buildAndExpand(coupon.getId())
                                    .toUri())
                    .body(buildCreateCouponResponse(coupon));
        }
    }

    private CreateCouponResponse buildCreateCouponResponse(Coupon coupon) {
        return CreateCouponResponse.builder()
                .couponName(coupon.getCouponName())
                .maxUsage(coupon.getMaxUsage())
                .country(coupon.getCountry())
                .build();
    }

    private void saveCustomer(Customer customer, Coupon coupon) {
        customer.redeemCoupon(coupon);
        customerRepository.save(customer);
    }

    private void incrementCouponUsage(String couponName, String country) {
        couponRepository.updateCurrentUsageInCoupon(couponName, country);
    }
}
