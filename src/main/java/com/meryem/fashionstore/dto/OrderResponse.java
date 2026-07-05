package com.meryem.fashionstore.dto;

import com.meryem.fashionstore.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long orderId,
        Long userId,
        BigDecimal totalPrice,
        LocalDateTime orderDate,
        OrderStatus status,
        List<OrderItemResponse> items
) {}