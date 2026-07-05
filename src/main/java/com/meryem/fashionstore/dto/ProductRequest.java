package com.meryem.fashionstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Ürün adı boş bırakılamaz!")
        String name,

        @NotBlank(message = "Ürün açıklaması boş bırakılamaz!")
        String description,

        @NotNull(message = "Ürün fiyatı boş bırakılamaz!")
        @Min(value = 0, message = "Ürün fiyatı negatif olamaz!")
        BigDecimal price,

        @NotNull(message = "Ürün stoğu boş bırakılamaz!")
        @Min(value = 0, message = "Ürün stoğu negatif olamaz!")
        Integer stock,

        @NotNull(message = "Kategori seçilmelidir!")
        Long categoryId
) {}