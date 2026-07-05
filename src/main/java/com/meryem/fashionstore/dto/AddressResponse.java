package com.meryem.fashionstore.dto;

public record AddressResponse(
        Long id,
        String title,
        String city,
        String district,
        String fullAddress
) {}