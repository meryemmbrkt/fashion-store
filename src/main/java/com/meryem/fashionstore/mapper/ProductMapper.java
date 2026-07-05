package com.meryem.fashionstore.mapper;

import com.meryem.fashionstore.dto.ProductRequest;
import com.meryem.fashionstore.dto.ProductResponse;
import com.meryem.fashionstore.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request) {
        return Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
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