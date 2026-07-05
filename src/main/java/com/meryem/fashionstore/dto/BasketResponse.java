package com.meryem.fashionstore.dto;

import java.math.BigDecimal;
import java.util.List;

public record BasketResponse(
        Long basketId,
        Long userId,
        BigDecimal totalPrice,
        List<BasketItemResponse> items
) {}

