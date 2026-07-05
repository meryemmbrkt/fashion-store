package com.meryem.fashionstore.mapper;

import com.meryem.fashionstore.dto.BasketItemResponse;
import com.meryem.fashionstore.dto.BasketResponse;
import com.meryem.fashionstore.entity.Basket;
import com.meryem.fashionstore.entity.BasketItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BasketMapper {

    public BasketItemResponse toItemResponse(BasketItem item) {
        return new BasketItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getQuantity()
        );
    }

    public BasketResponse toResponse(Basket basket) {
        List<BasketItemResponse> itemResponses = basket.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        return new BasketResponse(
                basket.getId(),
                basket.getUser().getId(),
                basket.getTotalPrice(),
                itemResponses
        );
    }
}