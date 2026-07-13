package com.meryem.fashionstore.controller;

import com.meryem.fashionstore.dto.OrderResponse;
import com.meryem.fashionstore.entity.OrderStatus;
import com.meryem.fashionstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping("/user/{userId}")
    public ResponseEntity<OrderResponse> createOrder(
            @PathVariable Long userId,
            @RequestParam Long addressId) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrderFromBasket(userId, addressId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable Long userId) {

        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }


    @PreAuthorize("hasRole('STORE_ADMIN')")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(orderService.updateOrderStatus(orderId, orderStatus));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Geçersiz sipariş durumu: " + status +
                            ". Geçerli değerler: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED"
            );
        }
    }
}