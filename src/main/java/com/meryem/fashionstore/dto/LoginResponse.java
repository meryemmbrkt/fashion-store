package com.meryem.fashionstore.dto;

import com.meryem.fashionstore.entity.Role;

public record LoginResponse(
        String token,
        String refreshToken,
        Long userId,
        String email,
        Role role
) {}