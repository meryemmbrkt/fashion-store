package com.meryem.fashionstore.controller;

import com.meryem.fashionstore.entity.ProductLog;
import com.meryem.fashionstore.service.ProductLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-logs")
@RequiredArgsConstructor
public class ProductLogController {

    private final ProductLogService productLogService;

    @PreAuthorize("hasRole('STORE_ADMIN')")
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductLog>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productLogService.getByProduct(productId));
    }

    @PreAuthorize("hasRole('STORE_ADMIN')")
    @GetMapping("/action/{action}")
    public ResponseEntity<List<ProductLog>> getByAction(@PathVariable String action) {
        return ResponseEntity.ok(productLogService.getByAction(action));
    }

    @PreAuthorize("hasRole('STORE_ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProductLog>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(productLogService.getByUser(userId));
    }
}
