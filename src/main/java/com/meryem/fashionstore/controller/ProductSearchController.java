package com.meryem.fashionstore.controller;

import com.meryem.fashionstore.document.ProductDocument;
import com.meryem.fashionstore.service.ProductSearchService;
import com.meryem.fashionstore.service.ProductSearchSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search-test")
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService productSearchService;
    private final ProductSearchSyncService productSearchSyncService;

    @GetMapping
    public Page<ProductDocument> searchProducts(
            @RequestParam String query,
            Pageable pageable
    ) {
        return productSearchService.searchProducts(
                query,
                pageable
        );
    }

    @PostMapping
    public ProductDocument save(
            @RequestBody ProductDocument productDocument
    ) {
        return productSearchService.save(productDocument);
    }

    @PostMapping("/sync")
    public String syncProducts() {

        int count =
                productSearchSyncService.syncProductsToElasticsearch();

        return count + " products indexed into Elasticsearch";
    }

    @GetMapping("/search-filter")
    public Page<ProductDocument> searchWithPriceFilter(
            @RequestParam String query,
            @RequestParam Double maxPrice,
            Pageable pageable
    ) {
        return productSearchService.searchProductsWithPriceFilter(
                query,
                maxPrice,
                pageable
        );
    }

    @GetMapping("/fuzzy")
    public Page<ProductDocument> searchProductsFuzzy(
            @RequestParam String query,
            Pageable pageable
    ) {
        return productSearchService.searchProductsFuzzy(
                query,
                pageable
        );
    }

    @GetMapping("/should")
    public Page<ProductDocument> searchProductsWithShould(
            @RequestParam String query,
            Pageable pageable
    ) {
        return productSearchService.searchProductsWithShould(
                query,
                pageable
        );
    }
}