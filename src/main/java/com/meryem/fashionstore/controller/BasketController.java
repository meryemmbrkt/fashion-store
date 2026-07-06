package com.meryem.fashionstore.controller;

import com.meryem.fashionstore.dto.BasketItemRequest;
import com.meryem.fashionstore.dto.BasketResponse;
import com.meryem.fashionstore.service.BasketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/baskets")
@RequiredArgsConstructor
public class BasketController {

    private final BasketService basketService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<BasketResponse> getBasketByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(basketService.getOrCreateBasket(userId));
    }

    @PostMapping("/{userId}/add/{productId}")
    public ResponseEntity<BasketResponse> addProductToBasket(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(basketService.addProductToBasket(userId, productId, quantity));
    }

    // Sepet kalemini yönetir: adedi set eder, yoksa ekler, 0 ise çıkarır
    @PutMapping("/{basketId}/items")
    public ResponseEntity<BasketResponse> updateItemQuantity(
            @PathVariable Long basketId,
            @Valid @RequestBody BasketItemRequest request) {
        return ResponseEntity.ok(basketService.updateItemQuantity(basketId, request));
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<BasketResponse> removeProductFromBasket(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        return ResponseEntity.ok(basketService.removeProductFromBasket(userId, productId));
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<BasketResponse> clearBasket(@PathVariable Long userId) {
        return ResponseEntity.ok(basketService.clearBasket(userId));
    }
}
// put apisi yazalim basket id si alsin oraya bir request yazalim product id ve kac tane alalim,