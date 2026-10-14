package com.meryem.fashionstore.dto;

import com.meryem.fashionstore.entity.Role;

public record UserFilterRequest(
        String email,
        Role role,
        int page,
        int size
) {}