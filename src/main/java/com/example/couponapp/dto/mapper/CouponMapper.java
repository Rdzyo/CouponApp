package com.example.couponapp.dto.mapper;

import com.example.couponapp.dto.request.CreateCouponRequest;
import com.example.couponapp.entity.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CouponMapper {

    CouponMapper INSTANCE = Mappers.getMapper( CouponMapper.class );

    @Mappings({
            @Mapping(source = "couponName", target = "couponName"),
            @Mapping(source = "maxUsage", target = "maxUsage"),
            @Mapping(source = "country", target = "country"),
            //Ignoring unmmaped properties can be omitted, but it will result in a warning during the build
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "currentUsage", ignore = true),
            @Mapping(target = "createdDate", ignore = true)
        }
    )
    Coupon createRequestToEntity(CreateCouponRequest createCouponRequest);
}
