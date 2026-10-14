/* package com.meryem.fashionstore.mapper;

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
} */

package com.meryem.fashionstore.mapper;

import com.meryem.fashionstore.dto.CategoryRequest;
import com.meryem.fashionstore.dto.CategoryResponse;
import com.meryem.fashionstore.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {


    @Mapping(target = "storeName", source = "store.storeName")
    CategoryResponse toResponse(Category category);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "store", source = "store")
    Category toEntity(CategoryRequest request,
                      com.meryem.fashionstore.entity.Store store);






}
