package com.meryem.fashionstore.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long itemId,
        Long productId,
        String productName,
        BigDecimal price,
        Integer quantity
) {}