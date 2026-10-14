package com.meryem.fashionstore.dto;

import java.util.List;

public record PagedUserResponse(
        List<UserResponse> users,
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize
) {}
