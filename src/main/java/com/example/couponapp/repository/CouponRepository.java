package com.example.couponapp.repository;

import com.example.couponapp.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    //might be used if an error message could be vague
    /*@Query(
            value = """
                SELECT CASE WHEN EXISTS(
                SELECT c FROM Coupon c
                WHERE c.coupon_name = :couponName AND c.country = :country AND c.max_usage > c.current_usage)
                THEN CAST(1 AS BIT)
                ELSE CAST(0 AS BIT) END
                """,
            nativeQuery = true
    )
    boolean couponExistsAndIsUsable(@Param("couponName") String couponName, @Param("country") String country);*/

    @Query(value = """
            SELECT EXISTS(
                SELECT 1 FROM customer_x_coupon cc
                WHERE cc.customer_id = :customerId AND cc.coupon_id = :couponId
                            )
            """,
            nativeQuery = true
    )
    boolean searchCouponIsRedeemedByCustomer(@Param("couponId") Long couponId, @Param("customerId") Long customerId);

    @Modifying
    @Query(
            value = """
                    UPDATE coupon
                    SET current_usage = current_usage + 1
                    WHERE coupon_name = :couponName
                    """,
            nativeQuery = true
    )
    void updateCurrentUsageInCoupon(@Param("couponName") String couponName);

    Optional<Coupon> findByCouponNameIsIgnoreCase(String couponName);

    boolean existsByCouponNameIsLikeIgnoreCase(String couponName);
}
