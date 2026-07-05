package com.meryem.fashionstore.dto;

import com.meryem.fashionstore.entity.Role;

public record LoginResponse(
        String token,
        Long userId,
        String email,
        Role role
) {}