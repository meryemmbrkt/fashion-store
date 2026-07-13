package com.meryem.fashionstore.mapper;

import com.meryem.fashionstore.dto.ProductRequest;
import com.meryem.fashionstore.dto.ProductResponse;
import com.meryem.fashionstore.entity.Category;
import com.meryem.fashionstore.entity.Product;
import com.meryem.fashionstore.entity.Store;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request, Store store, Category category) {
        return Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .store(store)
                .category(category)
                .build();
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getStore() != null ? product.getStore().getStoreName() : null,
                product.getCategory() != null ? product.getCategory().getName() : null
        );
    }
}