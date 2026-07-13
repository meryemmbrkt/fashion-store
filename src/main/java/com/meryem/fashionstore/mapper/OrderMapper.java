package com.meryem.fashionstore.mapper;

import com.meryem.fashionstore.dto.AddressResponse;
import com.meryem.fashionstore.dto.OrderItemResponse;
import com.meryem.fashionstore.dto.OrderResponse;
import com.meryem.fashionstore.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public Order toEntity(User user, java.math.BigDecimal totalPrice, Address deliveryAddress) {
        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.PENDING);
        order.setDeliveryAddress(deliveryAddress);
        return order;
    }

    public OrderItem toItemEntity(Order order, BasketItem basketItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(basketItem.getProduct());
        orderItem.setQuantity(basketItem.getQuantity());
        orderItem.setPrice(basketItem.getProduct().getPrice());
        return orderItem;
    }

    public OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getPrice(),
                item.getQuantity()
        );
    }

    public OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        AddressResponse addressResponse = null;
        if (order.getDeliveryAddress() != null) {
            addressResponse = new AddressResponse(
                    order.getDeliveryAddress().getId(),
                    order.getDeliveryAddress().getTitle(),
                    order.getDeliveryAddress().getCity(),
                    order.getDeliveryAddress().getDistrict(),
                    order.getDeliveryAddress().getFullAddress()
            );
        }

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getTotalPrice(),
                order.getOrderDate(),
                order.getStatus(),
                addressResponse,
                itemResponses
        );
    }
}