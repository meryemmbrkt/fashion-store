package com.meryem.fashionstore.dto;

import jakarta.validation.constraints.NotBlank;

public record CityRequest(

        @NotBlank(message = "City name cannot be empty")
        String name


) {
}