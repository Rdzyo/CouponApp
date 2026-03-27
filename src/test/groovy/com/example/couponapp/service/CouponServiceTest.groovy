package com.example.couponapp.service

import com.example.couponapp.dto.request.CreateCouponRequest
import com.example.couponapp.entity.Coupon
import com.example.couponapp.repository.CouponRepository
import org.springframework.http.HttpStatus
import spock.lang.Specification

class CouponServiceTest extends Specification {

    CouponRepository couponRepository
    CouponService couponService

    def setup() {
        couponRepository = Mock(CouponRepository)
        couponService = new CouponServiceImpl(couponRepository)
    }

    def "Should save object and return valid response"() {
        given:
        var couponName = "test"
        var maxUsage = 1
        var country = "PL"
        var request = new CreateCouponRequest(couponName, maxUsage, country)

        couponRepository.existsByCouponNameIsLikeIgnoreCaseAndCountry(_ as String, _ as String) >> false
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
        couponRepository.existsByCouponNameIsLikeIgnoreCaseAndCountry(_ as String, _ as String) >> true

        when:
        var response = couponService.createCoupon(request)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.getBody().errorMessage == "Coupon with that name already exists"
        0 * couponRepository.save(_ as Coupon)
    }
}
