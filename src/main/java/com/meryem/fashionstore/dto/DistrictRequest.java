package com.meryem.fashionstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DistrictRequest(

        @NotBlank(message = "District name cannot be empty")
        String name,

        @NotNull(message = "City id cannot be null")
        Long cityId

) {
}