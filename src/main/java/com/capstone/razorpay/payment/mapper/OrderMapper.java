package com.capstone.razorpay.payment.mapper;

import com.capstone.razorpay.payment.dto.response.OrderResponse;
import com.capstone.razorpay.payment.entity.OrderRecord;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    OrderResponse toResponse(OrderRecord orderRecord);
}
