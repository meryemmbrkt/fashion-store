package com.meryem.fashionstore.controller;

import com.meryem.fashionstore.dto.ProductRequest;
import com.meryem.fashionstore.dto.ProductResponse;
import com.meryem.fashionstore.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PreAuthorize("hasRole('STORE_ADMIN')")
    @PostMapping("/{storeId}/{categoryId}")
    public ResponseEntity<ProductResponse> addProduct(
            @PathVariable Long storeId,
            @PathVariable Long categoryId,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.addProductToStoreAndCategory(storeId, categoryId, request));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<ProductResponse>> getProductsByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(productService.getProductsByStore(storeId));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<ProductResponse>> getAllProductsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getAllProductsPaged(page, size));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PreAuthorize("hasRole('STORE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @PreAuthorize("hasRole('STORE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Ürün başarıyla silindi.");
    }
}