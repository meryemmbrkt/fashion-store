package com.meryem.fashionstore.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
        @NotBlank(message = "Adres başlığı boş bırakılamaz!")
        String title,

        @NotBlank(message = "Şehir boş bırakılamaz!")
        String city,

        @NotBlank(message = "İlçe boş bırakılamaz!")
        String district,

        @NotBlank(message = "Açık adres boş bırakılamaz!")
        String fullAddress
) {}