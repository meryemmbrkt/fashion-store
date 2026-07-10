package com.meryem.fashionstore.service;

import com.meryem.fashionstore.dto.BasketItemRequest;
import com.meryem.fashionstore.dto.BasketResponse;
import com.meryem.fashionstore.entity.Basket;
import com.meryem.fashionstore.entity.BasketItem;
import com.meryem.fashionstore.entity.Product;
import com.meryem.fashionstore.entity.User;
import com.meryem.fashionstore.mapper.BasketMapper;
import com.meryem.fashionstore.repository.BasketRepository;
import com.meryem.fashionstore.repository.ProductRepository;
import com.meryem.fashionstore.repository.UserRepository;
import com.meryem.fashionstore.exception.ResourceNotFoundException;
import com.meryem.fashionstore.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasketService {

    private final BasketRepository basketRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BasketMapper basketMapper;
    private final SecurityUtil securityUtil;

    @Transactional
    public BasketResponse getOrCreateBasket(Long userId) {
        securityUtil.checkUserAccess(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found! userId={}", userId);
                    return new ResourceNotFoundException("Kullanıcı bulunamadı!");
                });

        Basket basket = basketRepository.findByUserId(userId)
                .orElseGet(() -> {
                    log.info("Creating new basket for userId={}", userId);
                    Basket newBasket = new Basket();
                    newBasket.setUser(user);
                    return basketRepository.save(newBasket);
                });

        return basketMapper.toResponse(basket);
    }

    @Transactional
    public BasketResponse addProductToBasket(Long userId, Long productId, Integer quantity) {
        securityUtil.checkUserAccess(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found! userId={}", userId);
                    return new ResourceNotFoundException("Kullanıcı bulunamadı!");
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found! productId={}", productId);
                    return new ResourceNotFoundException("Eklenecek ürün bulunamadı!");
                });

        if (product.getStock() < quantity) {
            log.warn("Insufficient stock for basket add: productId={}, requested={}, available={}",
                    productId, quantity, product.getStock());
            throw new IllegalArgumentException(
                    "Stokta yeterli ürün yok! Mevcut stok: " + product.getStock()
            );
        }

        Basket basket = basketRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Basket newBasket = new Basket();
                    newBasket.setUser(user);
                    return basketRepository.save(newBasket);
                });

        Optional<BasketItem> existingItem = basket.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            BasketItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            BasketItem newItem = new BasketItem();
            newItem.setBasket(basket);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            basket.getItems().add(newItem);
        }

        recalculateTotalPrice(basket);

        Basket savedBasket = basketRepository.save(basket);
        log.info("Product added to basket: userId={}, productId={}, quantity={}",
                userId, productId, quantity);
        return basketMapper.toResponse(savedBasket);
    }

    @Transactional
    public BasketResponse updateItemQuantity(Long basketId, BasketItemRequest request) {
        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> {
                    log.error("Basket not found! basketId={}", basketId);
                    return new ResourceNotFoundException("Sepet bulunamadı!");
                });

        securityUtil.checkUserAccess(basket.getUser().getId());

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> {
                    log.error("Product not found! productId={}", request.productId());
                    return new ResourceNotFoundException("Ürün bulunamadı!");
                });

        if (request.quantity() > 0 && product.getStock() < request.quantity()) {
            log.warn("Insufficient stock for quantity update: productId={}, requested={}, available={}",
                    request.productId(), request.quantity(), product.getStock());
            throw new IllegalArgumentException(
                    "Stokta yeterli ürün yok! Mevcut stok: " + product.getStock()
            );
        }

        Optional<BasketItem> existingItem = basket.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(request.productId()))
                .findFirst();

        if (request.quantity() == 0) {
            existingItem.ifPresent(item -> basket.getItems().remove(item));
            log.info("Item removed from basket via quantity=0: basketId={}, productId={}",
                    basketId, request.productId());
        } else if (existingItem.isPresent()) {
            existingItem.get().setQuantity(request.quantity());
        } else {
            BasketItem newItem = new BasketItem();
            newItem.setBasket(basket);
            newItem.setProduct(product);
            newItem.setQuantity(request.quantity());
            basket.getItems().add(newItem);
        }

        recalculateTotalPrice(basket);

        Basket savedBasket = basketRepository.save(basket);
        log.info("Basket item quantity set: basketId={}, productId={}, quantity={}",
                basketId, request.productId(), request.quantity());
        return basketMapper.toResponse(savedBasket);
    }

    @Transactional
    public BasketResponse removeProductFromBasket(Long userId, Long productId) {
        securityUtil.checkUserAccess(userId);

        Basket basket = basketRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Basket not found for userId={}", userId);
                    return new ResourceNotFoundException("Bu kullanıcıya ait sepet bulunamadı!");
                });

        BasketItem itemToRemove = basket.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Item not found in basket: userId={}, productId={}", userId, productId);
                    return new ResourceNotFoundException("Sepette bu ürün bulunamadı!");
                });

        basket.getItems().remove(itemToRemove);
        recalculateTotalPrice(basket);

        Basket updatedBasket = basketRepository.save(basket);
        log.info("Product removed from basket: userId={}, productId={}", userId, productId);
        return basketMapper.toResponse(updatedBasket);
    }

    @Transactional
    public BasketResponse clearBasket(Long userId) {
        securityUtil.checkUserAccess(userId);

        Basket basket = basketRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Basket not found for userId={}", userId);
                    return new ResourceNotFoundException("Bu kullanıcıya ait sepet bulunamadı!");
                });

        basket.getItems().clear();
        basket.setTotalPrice(BigDecimal.ZERO);

        Basket updatedBasket = basketRepository.save(basket);
        log.info("Basket cleared: userId={}", userId);
        return basketMapper.toResponse(updatedBasket);
    }

    private void recalculateTotalPrice(Basket basket) {
        BigDecimal total = basket.getItems().stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        basket.setTotalPrice(total);
    }
}