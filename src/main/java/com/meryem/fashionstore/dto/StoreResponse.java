package com.meryem.fashionstore.dto;

public record StoreResponse(
        Long id,
        String storeName,
        String link,
        String ownerName
) {}