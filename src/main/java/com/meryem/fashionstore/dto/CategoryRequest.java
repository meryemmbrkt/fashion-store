package com.meryem.fashionstore.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank(message = "Kategori adı boş bırakılamaz!")
        String name
) {}
