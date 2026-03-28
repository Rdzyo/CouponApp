package com.example.couponapp.util.validation

import com.example.couponapp.entity.Coupon
import com.example.couponapp.repository.CouponRepository
import com.example.couponapp.util.CouponMessagesUtil
import spock.lang.Specification

import java.time.Instant

class CouponValidatorTest extends Specification {

    CouponRepository couponRepository
    CouponValidator couponValidator

    def setup() {
        couponRepository = Mock(CouponRepository)
        couponValidator = new CouponValidator(couponRepository)
    }

    def "Should return body with error message that coupon already exist"() {
        given:
        var country = "PL"
        Optional<Coupon> couponOpt = Optional.empty()

        when:
        var body = couponValidator.validateRedeemCouponRequest(country, couponOpt)

        then:
        body.errorMessage == CouponMessagesUtil.COUPON_DOES_NOT_EXIST
    }

    def "Should return body with error message that coupon is not available for that country"() {
        given:
        var country = "US"
        var couponOpt = Optional.of(testCoupon(0))

        when:
        var body = couponValidator.validateRedeemCouponRequest(country, couponOpt)

        then:
        body.errorMessage == CouponMessagesUtil.COUPON_IS_NOT_AVAILABLE_FOR_COUNTRY
    }

    def "Should return body with error message that coupon has been expired"() {
        given:
        var country = "PL"
        var couponOpt = Optional.of(testCoupon(10))

        when:
        var body = couponValidator.validateRedeemCouponRequest(country, couponOpt)

        then:
        body.errorMessage == CouponMessagesUtil.COUPON_MAX_USAGE_REACHED
    }

    static testCoupon(int currentUsage) {
        return new Coupon(id: 1L, couponName: "test", country: "PL", maxUsage: 10, currentUsage: currentUsage, createdDate: Instant.now())
    }
}
