package com.example.couponapp.repository;

import com.example.couponapp.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    boolean existsByCouponNameIsLikeIgnoreCaseAndCountry(String couponName, String country);
}
