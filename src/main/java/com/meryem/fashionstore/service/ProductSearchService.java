package com.meryem.fashionstore.service;

import com.meryem.fashionstore.document.ProductDocument;
import com.meryem.fashionstore.repository.ProductRepository;
import com.meryem.fashionstore.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductSearchRepository productSearchRepository;
    private final ProductRepository productRepository;

    public ProductDocument save(ProductDocument productDocument) {
        return productSearchRepository.save(productDocument);
    }

    public Page<ProductDocument> searchProducts(
            String query,
            Pageable pageable
    ) {
        return productSearchRepository.searchProducts(
                query,
                pageable
        );
    }

    public Page<ProductDocument> searchProductsWithPriceFilter(
            String query,
            Double maxPrice,
            Pageable pageable
    ) {
        return productSearchRepository.searchProductsWithPriceFilter(
                query,
                maxPrice,
                pageable
        );
    }

    public Page<ProductDocument> searchProductsFuzzy(
            String query,
            Pageable pageable
    ) {
        return productSearchRepository.searchProductsFuzzy(
                query,
                pageable
        );
    }

    public Page<ProductDocument> searchProductsWithShould(
            String query,
            Pageable pageable
    ) {
        return productSearchRepository.searchProductsWithShould(
                query,
                pageable
        );
    }
}