package com.meryem.fashionstore.mapper;

import com.meryem.fashionstore.dto.AddressResponse;
import com.meryem.fashionstore.dto.OrderItemResponse;
import com.meryem.fashionstore.dto.OrderResponse;
import com.meryem.fashionstore.entity.Order;
import com.meryem.fashionstore.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

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