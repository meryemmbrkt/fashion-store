package com.meryem.fashionstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BasketItemRequest(
        @NotNull(message = "Ürün seçilmelidir!")
        Long productId,

        @NotNull(message = "Adet boş bırakılamaz!")
        @Min(value = 0, message = "Adet negatif olamaz!")
        Integer quantity
) {}