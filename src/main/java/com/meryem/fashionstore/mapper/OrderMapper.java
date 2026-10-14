/* package com.meryem.fashionstore.mapper;

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
} */
package com.meryem.fashionstore.mapper;

import com.meryem.fashionstore.dto.AddressResponse;
import com.meryem.fashionstore.dto.OrderItemResponse;
import com.meryem.fashionstore.dto.OrderResponse;
import com.meryem.fashionstore.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public abstract class OrderMapper {


    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "statusLabel", source = "status", qualifiedByName = "statusToLabel")
    @Mapping(target = "deliveryAddress", source = "deliveryAddress", qualifiedByName = "mapAddress")
    public abstract OrderResponse toResponse(Order order);


    @Mapping(target = "itemId", source = "id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    public abstract OrderItemResponse toItemResponse(OrderItem item);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "status", expression = "java(OrderStatus.PENDING)")
    public abstract Order toEntity(
            com.meryem.fashionstore.entity.User user,
            java.math.BigDecimal totalPrice,
            com.meryem.fashionstore.entity.Address deliveryAddress
    );


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price", source = "basketItem.product.price")
    @Mapping(target = "quantity", source = "basketItem.quantity")
    @Mapping(target = "product", source = "basketItem.product")
    public abstract OrderItem toItemEntity(Order order, BasketItem basketItem);


    @Named("statusToLabel")
    public String statusToLabel(OrderStatus status) {
        return switch (status) {
            case PENDING -> "Beklemede";
            case CONFIRMED -> "Onaylandı";
            case SHIPPED -> "Kargoya Verildi";
            case DELIVERED -> "Teslim Edildi";
            case CANCELLED -> "İptal Edildi";
        };
    }

    @Named("mapAddress")
    public AddressResponse mapAddress(Address address) {
        if (address == null) return null;
        return new AddressResponse(
                address.getId(),
                address.getTitle(),
                address.getCity(),
                address.getDistrict(),
                address.getFullAddress()
        );
    }
}