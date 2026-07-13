package com.meryem.fashionstore.mapper;

import com.meryem.fashionstore.dto.AddressRequest;
import com.meryem.fashionstore.dto.AddressResponse;
import com.meryem.fashionstore.entity.Address;
import com.meryem.fashionstore.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address toEntity(AddressRequest request, User customer) {
        Address address = new Address();
        address.setTitle(request.title());
        address.setCity(request.city());
        address.setDistrict(request.district());
        address.setFullAddress(request.fullAddress());
        address.setCustomer(customer);
        return address;
    }

    public AddressResponse toResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getTitle(),
                address.getCity(),
                address.getDistrict(),
                address.getFullAddress()
        );
    }
}