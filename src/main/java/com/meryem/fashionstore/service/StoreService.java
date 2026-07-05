package com.meryem.fashionstore.service;

import com.meryem.fashionstore.dto.StoreRequest;
import com.meryem.fashionstore.dto.StoreResponse;
import com.meryem.fashionstore.entity.Store;
import com.meryem.fashionstore.entity.User;
import com.meryem.fashionstore.exception.ResourceNotFoundException;
import com.meryem.fashionstore.mapper.StoreMapper;
import com.meryem.fashionstore.repository.StoreRepository;
import com.meryem.fashionstore.repository.UserRepository;
import com.meryem.fashionstore.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final StoreMapper storeMapper;
    private final SecurityUtil securityUtil;

    public List<StoreResponse> getAllStores() {
        return storeRepository.findAll().stream()
                .map(storeMapper::toResponse)
                .toList();
    }

    @Transactional
    public StoreResponse createStore(StoreRequest request, Long userId) {
        securityUtil.checkUserAccess(userId);

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mağaza açmak isteyen kullanıcı sistemde bulunamadı!"
                ));

        if (storeRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("Bu kullanıcının zaten bir mağazası var!");
        }

        Store store = storeMapper.toEntity(request);
        store.setUser(owner);

        return storeMapper.toResponse(storeRepository.save(store));
    }

    public StoreResponse getStoreById(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aradığınız mağaza sistemde bulunamadı!"
                ));
        return storeMapper.toResponse(store);
    }

    public StoreResponse getStoreByUserId(Long userId) {
        Store store = storeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bu kullanıcıya ait mağaza bulunamadı!"
                ));
        return storeMapper.toResponse(store);
    }

    @Transactional
    public void deleteStore(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Silmek istediğiniz mağaza bulunamadı!"
                ));

        securityUtil.checkStoreOwnership(store);

        if (!store.getProducts().isEmpty()) {
            throw new IllegalArgumentException(
                    "Bu mağazada ürünler var! Mağazayı silmek için önce ürünleri silin."
            );
        }

        storeRepository.delete(store);
    }
}