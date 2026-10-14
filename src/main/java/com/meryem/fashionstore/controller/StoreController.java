package com.meryem.fashionstore.controller;

import com.meryem.fashionstore.dto.StoreRequest;
import com.meryem.fashionstore.dto.StoreResponse;
import com.meryem.fashionstore.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PreAuthorize("hasRole('STORE_ADMIN')")
    @PostMapping("/{userId}")
    public ResponseEntity<StoreResponse> createStore(@PathVariable Long userId, @Valid @RequestBody StoreRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(storeService.createStore(request, userId));
    }

    @GetMapping
    public ResponseEntity<List<StoreResponse>> getAllStores() {

        return ResponseEntity.ok(storeService.getAllStores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreResponse> getStoreById(@PathVariable Long id) {

        return ResponseEntity.ok(storeService.getStoreById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<StoreResponse> getStoreByUserId(@PathVariable Long userId) {

        return ResponseEntity.ok(storeService.getStoreByUserId(userId));
    }

    @PreAuthorize("hasRole('STORE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<StoreResponse> updateStore(@PathVariable Long id, @Valid @RequestBody StoreRequest request) {

        return ResponseEntity.ok(storeService.updateStore(id, request));
    }

    @PreAuthorize("hasRole('STORE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStore(@PathVariable Long id) {

        storeService.deleteStore(id);

        return ResponseEntity.ok("Mağaza başarıyla silindi.");
    }
}