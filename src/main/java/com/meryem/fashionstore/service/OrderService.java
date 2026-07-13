package com.meryem.fashionstore.service;

import com.meryem.fashionstore.dto.OrderResponse;
import com.meryem.fashionstore.entity.*;
import com.meryem.fashionstore.mapper.OrderMapper;
import com.meryem.fashionstore.repository.AddressRepository;
import com.meryem.fashionstore.repository.BasketRepository;
import com.meryem.fashionstore.repository.OrderRepository;
import com.meryem.fashionstore.repository.ProductRepository;
import com.meryem.fashionstore.repository.UserRepository;
import com.meryem.fashionstore.exception.ResourceNotFoundException;
import com.meryem.fashionstore.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BasketRepository basketRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final SecurityUtil securityUtil;
    private final AddressRepository addressRepository;

    @Transactional
    public OrderResponse createOrderFromBasket(Long userId, Long addressId) {
        securityUtil.checkUserAccess(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı!"));

        Address deliveryAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Teslimat adresi bulunamadı!"));

        if (!deliveryAddress.getCustomer().getId().equals(userId)) {
            throw new AccessDeniedException("Bu adres size ait değil!");
        }

        Basket basket = basketRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bu kullanıcıya ait sepet bulunamadı!"
                ));

        if (basket.getItems().isEmpty()) {
            throw new IllegalArgumentException("Boş sepet ile sipariş oluşturulamaz!");
        }

        Order order = orderMapper.toEntity(user, basket.getTotalPrice(), deliveryAddress);

        List<OrderItem> orderItems = new ArrayList<>();
        for (BasketItem basketItem : basket.getItems()) {
            OrderItem orderItem = orderMapper.toItemEntity(order, basketItem);
            orderItems.add(orderItem);

            productService.decreaseStock(
                    basketItem.getProduct().getId(),
                    basketItem.getQuantity()
            );
        }
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        basket.getItems().clear();
        basket.setTotalPrice(BigDecimal.ZERO);
        basketRepository.save(basket);

        return orderMapper.toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        securityUtil.checkUserAccess(userId);
        List<Order> orders = orderRepository.findByUserIdOrderByOrderDateDesc(userId);
        return orders.stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sipariş bulunamadı!"));

        Long currentUserId = securityUtil.getCurrentUser().getId();
        boolean hasOwnProduct = order.getItems().stream()
                .anyMatch(item -> item.getProduct().getStore().getUser().getId()
                        .equals(currentUserId));
        if (!hasOwnProduct) {
            throw new AccessDeniedException("Bu siparişte size ait ürün bulunmuyor!");
        }

        if (status == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
            order.getItems().forEach(orderItem -> {
                Product product = orderItem.getProduct();
                product.setStock(product.getStock() + orderItem.getQuantity());
                productRepository.save(product);
            });
        }

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponse(updatedOrder);
    }
}