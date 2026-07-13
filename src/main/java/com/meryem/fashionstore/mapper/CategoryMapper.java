package com.meryem.fashionstore.mapper;

import com.meryem.fashionstore.dto.CategoryRequest;
import com.meryem.fashionstore.dto.CategoryResponse;
import com.meryem.fashionstore.entity.Category;
import com.meryem.fashionstore.entity.Store;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request, Store store) {
        return Category.builder()
                .name(request.name())
                .store(store)
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