package com.meryem.fashionstore.mapper;

import com.meryem.fashionstore.dto.StoreRequest;
import com.meryem.fashionstore.dto.StoreResponse;
import com.meryem.fashionstore.entity.Store;
import org.springframework.stereotype.Component;

@Component
public class StoreMapper {

    public Store toEntity(StoreRequest request) {
        return Store.builder()
                .storeName(request.storeName())
                .link(request.link())
                .build();
    }


    public StoreResponse toResponse(Store store) {
        String ownerName = store.getUser() != null
                ? store.getUser().getFirstName() + " " + store.getUser().getLastName()
                : null;

        return new StoreResponse(
                store.getId(),
                store.getStoreName(),
                store.getLink(),
                ownerName
        );
    }
}