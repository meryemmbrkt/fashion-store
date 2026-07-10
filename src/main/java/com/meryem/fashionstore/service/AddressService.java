package com.meryem.fashionstore.service;

import com.meryem.fashionstore.dto.AddressRequest;
import com.meryem.fashionstore.dto.AddressResponse;
import com.meryem.fashionstore.entity.Address;
import com.meryem.fashionstore.entity.User;
import com.meryem.fashionstore.exception.ResourceNotFoundException;
import com.meryem.fashionstore.mapper.AddressMapper;
import com.meryem.fashionstore.repository.AddressRepository;
import com.meryem.fashionstore.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final SecurityUtil securityUtil;

    @Transactional
    public AddressResponse createAddress(AddressRequest request) {
        User currentUser = securityUtil.getCurrentUser();

        Address address = addressMapper.toEntity(request);
        address.setCustomer(currentUser);

        return addressMapper.toResponse(addressRepository.save(address));
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getMyAddresses() {
        User currentUser = securityUtil.getCurrentUser();
        return addressRepository.findByCustomerId(currentUser.getId()).stream()
                .map(addressMapper::toResponse)
                .toList();
    }

    @Transactional
    public void deleteAddress(Long id) {
        User currentUser = securityUtil.getCurrentUser();

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adres bulunamadı!"));

        if (!address.getCustomer().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Bu adres size ait değil!");
        }

        addressRepository.delete(address);
    }
}