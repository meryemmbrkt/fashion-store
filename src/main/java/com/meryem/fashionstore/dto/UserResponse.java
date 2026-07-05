package com.meryem.fashionstore.dto;

import com.meryem.fashionstore.entity.Role;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Role role
) {}