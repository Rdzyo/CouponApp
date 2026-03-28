package com.example.couponapp.service

import com.example.couponapp.dto.request.CreateCouponRequest
import com.example.couponapp.dto.request.RedeemCouponRequest
import com.example.couponapp.dto.response.RedeemCouponResponse
import com.example.couponapp.entity.Coupon
import com.example.couponapp.repository.CouponRepository
import com.example.couponapp.util.CouponMessagesUtil
import com.example.couponapp.util.GeolocationUtil
import com.example.couponapp.util.validation.CouponValidator
import org.springframework.http.HttpStatus
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification

import java.time.Instant

class CouponServiceTest extends Specification {

    CouponRepository couponRepository
    CouponValidator couponValidator
    CouponService couponService

    def setup() {
        couponRepository = Mock(CouponRepository)
        couponValidator = Mock(CouponValidator)
        couponService = new CouponServiceImpl(couponRepository, couponValidator)
        ReflectionTestUtils.setField(GeolocationUtil.class, "GEO_COUNTRY_FILE_PATH", "src/main/resources/ipDb/GeoLite2-Country.mmdb")
    }

    def "Should save object and return valid response"() {
        given:
        var couponName = "test"
        var maxUsage = 1
        var country = "PL"
        var request = new CreateCouponRequest(couponName, maxUsage, country)

        couponValidator.couponAlreadyExist(_ as String) >> false
        couponRepository.save(_ as Coupon) >> {
            var coupon = new Coupon()
            coupon.setId(1)
            coupon.setCouponName(couponName)
            coupon.setMaxUsage(maxUsage)
            coupon.setCurrentUsage(0)
            coupon.setCountry(country)
            return coupon
        }

        when:
        var response = couponService.createCoupon(request)

        then:
        var body = response.getBody()
        response.statusCode == HttpStatus.CREATED
        body.couponName == couponName
        body.country == country
        body.maxUsage == maxUsage
    }

    def "Should return 400 and message that object exists"() {
        given:
        var couponName = "test"
        var maxUsage = 1
        var country = "PL"
        var request = new CreateCouponRequest(couponName, maxUsage, country)
        couponValidator.couponAlreadyExist(_ as String) >> true

        when:
        var response = couponService.createCoupon(request)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.getBody().errorMessage == CouponMessagesUtil.COUPON_ALREADY_EXIST
        0 * couponRepository.save(_ as Coupon)
    }

    def "Should redeem coupon successfully"() {
        given:
        var customerId = 1L
        var couponName = "test"
        var ipAddr = "217.119.64.172"
        var request = new RedeemCouponRequest(customerId, couponName, ipAddr)
        couponRepository.findByCouponNameIsIgnoreCase(_ as String) >> Optional.of(testCoupon())
        couponValidator.validateRedeemCouponRequest(_ as String, _ as Optional) >> RedeemCouponResponse.builder().build()

        when:
        var response = couponService.redeemCoupon(request)

        then:
        response.statusCode == HttpStatus.OK
        response.getBody().successMessage == CouponMessagesUtil.COUPON_REDEEMED_SUCCESS
    }

    def "Should fail validation and return message that coupon does not exist"() {
        given:
        var customerId = 1L
        var couponName = "test"
        var ipAddr = "217.119.64.172"
        var request = new RedeemCouponRequest(customerId, couponName, ipAddr)
        couponRepository.findByCouponNameIsIgnoreCase(_ as String) >> Optional.of(testCoupon())
        couponValidator.validateRedeemCouponRequest(_ as String, _ as Optional) >> RedeemCouponResponse.builder().errorMessage(CouponMessagesUtil.COUPON_ALREADY_EXIST).build()

        when:
        var response = couponService.redeemCoupon(request)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.getBody().errorMessage == CouponMessagesUtil.COUPON_ALREADY_EXIST
    }

    def "Should fail validation and return message that coupon is not available for that country"() {
        given:
        var customerId = 1L
        var couponName = "test"
        var ipAddr = "217.119.64.172"
        var request = new RedeemCouponRequest(customerId, couponName, ipAddr)
        couponRepository.findByCouponNameIsIgnoreCase(_ as String) >> Optional.of(testCoupon())
        couponValidator.validateRedeemCouponRequest(_ as String, _ as Optional) >> RedeemCouponResponse.builder().errorMessage(CouponMessagesUtil.COUPON_IS_NOT_AVAILABLE_FOR_COUNTRY).build()

        when:
        var response = couponService.redeemCoupon(request)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.getBody().errorMessage == CouponMessagesUtil.COUPON_IS_NOT_AVAILABLE_FOR_COUNTRY
    }

    def "Should fail validation and return message that coupon has expired"() {
        given:
        var customerId = 1L
        var couponName = "test"
        var ipAddr = "217.119.64.172"
        var request = new RedeemCouponRequest(customerId, couponName, ipAddr)
        couponRepository.findByCouponNameIsIgnoreCase(_ as String) >> Optional.of(testCoupon())
        couponValidator.validateRedeemCouponRequest(_ as String, _ as Optional) >> RedeemCouponResponse.builder().errorMessage(CouponMessagesUtil.COUPON_MAX_USAGE_REACHED).build()

        when:
        var response = couponService.redeemCoupon(request)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.getBody().errorMessage == CouponMessagesUtil.COUPON_MAX_USAGE_REACHED
    }

    static testCoupon() {
        return new Coupon(id: 1L, couponName: "test", country: "PL", maxUsage: 10, currentUsage: 0, createdDate: Instant.now())
    }
}
