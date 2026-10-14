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
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final StoreMapper storeMapper;
    private final SecurityUtil securityUtil;
    private final CacheManager cacheManager;

    private final RedisTemplate<String, StoreResponse>
            storeRedisTemplate;


    // =========================================================
    // GET ALL STORES
    // =========================================================

    @Transactional(readOnly = true)
    public List<StoreResponse> getAllStores() {

        log.info(
                "GET ALL STORES - Checking Redis"
        );


        List<StoreResponse> storesFromRedis =
                new ArrayList<>();


        // =====================================================
        // REDIS SCAN
        // =====================================================

        ScanOptions scanOptions =
                ScanOptions.scanOptions()
                        .match("stores::*")
                        .count(1000)
                        .build();


        try (
                Cursor<String> cursor =
                        storeRedisTemplate.scan(scanOptions)
        ) {

            while (cursor.hasNext()) {

                String key =
                        cursor.next();


                StoreResponse store =
                        storeRedisTemplate
                                .opsForValue()
                                .get(key);


                if (store != null) {

                    storesFromRedis.add(store);
                }
            }
        }


        // =====================================================
        // REDIS'TEN VERİ GELDİ
        // =====================================================

        if (!storesFromRedis.isEmpty()) {

            storesFromRedis.sort(
                    Comparator.comparing(
                            StoreResponse::id
                    )
            );

            log.info(
                    "GET ALL STORES - {} stores loaded from REDIS",
                    storesFromRedis.size()
            );

            return storesFromRedis;
        }


        // =====================================================
        // REDIS BOŞ → DATABASE
        // =====================================================

        log.info(
                "GET ALL STORES - Redis empty, reading from DATABASE"
        );


        List<StoreResponse> stores =
                storeRepository
                        .findAllByActiveTrue()
                        .stream()
                        .map(storeMapper::toResponse)
                        .toList();


        // =====================================================
        // DATABASE'DEN GELEN MAĞAZALARI REDIS'E TEK TEK YAZ
        // =====================================================

        /*
         * Her mağaza ayrı ayrı cache'leniyor.
         *
         * stores::1 → Store 1
         * stores::2 → Store 2
         * stores::3 → Store 3
         */

        Cache storeCache =
                cacheManager.getCache("stores");


        if (storeCache != null) {

            for (StoreResponse store : stores) {

                storeCache.put(
                        store.id(),
                        store
                );
            }
        }


        log.info(
                "GET ALL STORES - {} stores loaded from DATABASE and individually cached",
                stores.size()
        );


        return stores;
    }


    // =========================================================
    // CREATE STORE
    // =========================================================

    @Transactional
    @CacheEvict(
            value = {
                    "stores",
                    "storesByUser"
            },
            allEntries = true
    )
    public StoreResponse createStore(
            StoreRequest request,
            Long userId
    ) {

        securityUtil.checkUserAccess(userId);

        User owner =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Mağaza açmak isteyen kullanıcı sistemde bulunamadı!"
                                )
                        );


        if (storeRepository
                .findByUserIdAndActiveTrue(userId)
                .isPresent()) {

            throw new IllegalArgumentException(
                    "Bu kullanıcının zaten bir mağazası var!"
            );
        }


        Store store =
                storeMapper.toEntity(
                        request,
                        owner
                );


        Store savedStore =
                storeRepository.save(store);


        log.info(
                "Store created: id={}, name={}, userId={}",
                savedStore.getId(),
                savedStore.getStoreName(),
                userId
        );


        return storeMapper.toResponse(savedStore);
    }


    // =========================================================
    // GET STORE BY ID
    // =========================================================

    @Transactional(readOnly = true)
    @Cacheable(
            value = "stores",
            key = "#id"
    )
    public StoreResponse getStoreById(Long id) {

        log.info(
                "STORE CACHE MISS - Reading store from DATABASE: id={}",
                id
        );


        Store store =
                storeRepository
                        .findByIdAndActiveTrue(id)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Aradığınız mağaza sistemde bulunamadı!"
                                )
                        );


        log.info(
                "Store loaded from DATABASE: id={}, name={}",
                store.getId(),
                store.getStoreName()
        );


        return storeMapper.toResponse(store);
    }


    // =========================================================
    // GET STORE BY USER ID
    // =========================================================

    @Transactional(readOnly = true)
    @Cacheable(
            value = "storesByUser",
            key = "#userId"
    )
    public StoreResponse getStoreByUserId(
            Long userId
    ) {

        log.info(
                "STORE BY USER CACHE MISS - Reading from DATABASE: userId={}",
                userId
        );


        Store store =
                storeRepository
                        .findByUserIdAndActiveTrue(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Bu kullanıcıya ait mağaza bulunamadı!"
                                )
                        );


        return storeMapper.toResponse(store);
    }


    // =========================================================
    // UPDATE STORE
    // =========================================================

    @Transactional
    @CachePut(
            value = "stores",
            key = "#id"
    )
    @CacheEvict(
            value = "storesByUser",
            allEntries = true
    )
    public StoreResponse updateStore(
            Long id,
            StoreRequest request
    ) {

        log.info(
                "STORE UPDATE - Updating store in DATABASE: id={}",
                id
        );


        Store store =
                storeRepository
                        .findByIdAndActiveTrue(id)
                        .orElseThrow(
                                () -> {

                                    log.error(
                                            "STORE UPDATE FAILED - Store not found: id={}",
                                            id
                                    );


                                    return new ResourceNotFoundException(
                                            "Güncellenmek istenen mağaza bulunamadı!"
                                    );
                                }
                        );


        securityUtil.checkStoreOwnership(store);


        store.setStoreName(
                request.storeName()
        );


        store.setLink(
                request.link()
        );


        Store updatedStore =
                storeRepository.save(store);


        log.info(
                "STORE UPDATED - DB updated and Redis cache will be updated: id={}, name={}",
                updatedStore.getId(),
                updatedStore.getStoreName()
        );


        return storeMapper.toResponse(updatedStore);
    }


    // =========================================================
    // DELETE STORE
    // =========================================================

    @Transactional
    @CacheEvict(
            value = {
                    "stores",
                    "storesByUser"
            },
            allEntries = true
    )
    public void deleteStore(Long id) {

        Store store =
                storeRepository
                        .findByIdAndActiveTrue(id)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Silmek istediğiniz mağaza bulunamadı!"
                                )
                        );


        securityUtil.checkStoreOwnership(store);


        if (!store.getProducts().isEmpty()) {

            throw new IllegalArgumentException(
                    "Bu mağazada ürünler var! Önce ürünleri silin."
            );
        }


        store.setActive(false);

        storeRepository.save(store);


        log.info(
                "Store soft deleted: id={}, name={}",
                id,
                store.getStoreName()
        );
    }
}