package com.meryem.fashionstore.mapper;

import com.meryem.fashionstore.dto.CategoryRequest;
import com.meryem.fashionstore.dto.CategoryResponse;
import com.meryem.fashionstore.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request) {
        return Category.builder()
                .name(request.name())
                .build();
    }

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getStore() != null ? category.getStore().getStoreName() : null
        );
    }
}