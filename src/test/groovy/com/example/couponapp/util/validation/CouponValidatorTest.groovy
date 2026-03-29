package com.example.couponapp.util.validation

import com.example.couponapp.entity.Coupon
import com.example.couponapp.repository.CouponRepository
import com.example.couponapp.util.message.CouponMessagesUtil
import com.example.couponapp.validation.CouponValidator
import spock.lang.Specification

import java.time.Instant

class CouponValidatorTest extends Specification {

    CouponRepository couponRepository
    CouponValidator couponValidator

    private static Long TEST_CUSTOMER_ID = 1L
    private static String TEST_COUNTRY = "PL"

    def setup() {
        couponRepository = Mock(CouponRepository)
        couponValidator = new CouponValidator(couponRepository)
    }

    def "Should return body with error message that coupon already exist"() {
        given:
        Optional<Coupon> couponOpt = Optional.empty()

        when:
        var body = couponValidator.validateRedeemCouponRequest(TEST_COUNTRY, couponOpt, TEST_CUSTOMER_ID)

        then:
        body.errorMessage == CouponMessagesUtil.COUPON_DOES_NOT_EXIST
    }

    def "Should return body with error message that coupon is not available for that country"() {
        given:
        var country = "US"
        var couponOpt = Optional.of(testCoupon(0))

        when:
        var body = couponValidator.validateRedeemCouponRequest(country, couponOpt, TEST_CUSTOMER_ID)

        then:
        body.errorMessage == CouponMessagesUtil.COUPON_IS_NOT_AVAILABLE_FOR_COUNTRY
    }

    def "Should return body with error message that coupon has been expired"() {
        given:
        var couponOpt = Optional.of(testCoupon(10))

        when:
        var body = couponValidator.validateRedeemCouponRequest(TEST_COUNTRY, couponOpt, TEST_CUSTOMER_ID)

        then:
        body.errorMessage == CouponMessagesUtil.COUPON_MAX_USAGE_REACHED
    }

    def "Should return that coupon was redeemed by user"() {
        given:
        var couponOpt = Optional.of(testCoupon(1))
        couponRepository.searchCouponIsRedeemedByCustomer(couponOpt.get().getId(), TEST_CUSTOMER_ID) >> true

        when:
        var body = couponValidator.validateRedeemCouponRequest(TEST_COUNTRY, couponOpt, TEST_CUSTOMER_ID)

        then:
        body.errorMessage == CouponMessagesUtil.COUPON_ALREADY_USED_BY_USER
    }

    static testCoupon(int currentUsage) {
        return new Coupon(id: 1L, couponName: "test", country: "PL", maxUsage: 10, currentUsage: currentUsage, createdDate: Instant.now())
    }
}
