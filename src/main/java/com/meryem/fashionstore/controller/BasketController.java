package com.meryem.fashionstore.controller;

import com.meryem.fashionstore.dto.BasketResponse;
import com.meryem.fashionstore.security.SecurityUtil;
import com.meryem.fashionstore.service.BasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/baskets")
@RequiredArgsConstructor
public class BasketController {

    private final BasketService basketService;
    private final SecurityUtil securityUtil;

    @GetMapping("/user/{userId}")
    public ResponseEntity<BasketResponse> getBasketByUserId(@PathVariable Long userId) {
        securityUtil.checkUserAccess(userId);
        return ResponseEntity.ok(basketService.getOrCreateBasket(userId));
    }

    @PostMapping("/{userId}/add/{productId}")
    public ResponseEntity<BasketResponse> addProductToBasket(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        securityUtil.checkUserAccess(userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(basketService.addProductToBasket(userId, productId, quantity));
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<BasketResponse> removeProductFromBasket(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        securityUtil.checkUserAccess(userId);
        return ResponseEntity.ok(basketService.removeProductFromBasket(userId, productId));
    }

    @PutMapping("/{userId}/decrease/{productId}")
    public ResponseEntity<BasketResponse> decreaseProductQuantity(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        securityUtil.checkUserAccess(userId);
        return ResponseEntity.ok(basketService.decreaseProductQuantity(userId, productId));
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<BasketResponse> clearBasket(@PathVariable Long userId) {
        securityUtil.checkUserAccess(userId);
        return ResponseEntity.ok(basketService.clearBasket(userId));
    }
}