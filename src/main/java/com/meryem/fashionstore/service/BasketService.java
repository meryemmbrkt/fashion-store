package com.meryem.fashionstore.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasketService {

    private final BasketRepository basketRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BasketMapper basketMapper;

    @Transactional
    public BasketResponse getOrCreateBasket(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı!"));

        Basket basket = basketRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Basket newBasket = new Basket();
                    newBasket.setUser(user);
                    return basketRepository.save(newBasket);
                });

        return basketMapper.toResponse(basket);
    }

    @Transactional
    public BasketResponse addProductToBasket(Long userId, Long productId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı!"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Eklenecek ürün bulunamadı!"));

        if (product.getStock() < quantity) {
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
        return basketMapper.toResponse(savedBasket);
    }

    @Transactional
    public BasketResponse removeProductFromBasket(Long userId, Long productId) {
        Basket basket = basketRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bu kullanıcıya ait sepet bulunamadı!"
                ));

        BasketItem itemToRemove = basket.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Sepette bu ürün bulunamadı!"));

        basket.getItems().remove(itemToRemove);

        recalculateTotalPrice(basket);

        Basket updatedBasket = basketRepository.save(basket);
        return basketMapper.toResponse(updatedBasket);
    }

    @Transactional
    public BasketResponse decreaseProductQuantity(Long userId, Long productId) {
        Basket basket = basketRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bu kullanıcıya ait sepet bulunamadı!"
                ));

        BasketItem item = basket.getItems().stream()
                .filter(basketItem -> basketItem.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Sepette bu ürün bulunamadı!"));

        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
        } else {
            basket.getItems().remove(item);
        }

        recalculateTotalPrice(basket);

        Basket updatedBasket = basketRepository.save(basket);
        return basketMapper.toResponse(updatedBasket);
    }

    @Transactional
    public BasketResponse clearBasket(Long userId) {
        Basket basket = basketRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bu kullanıcıya ait sepet bulunamadı!"
                ));

        basket.getItems().clear();
        basket.setTotalPrice(BigDecimal.ZERO);

        Basket updatedBasket = basketRepository.save(basket);
        return basketMapper.toResponse(updatedBasket);
    }

    // Sepet toplamını her zaman item'lardan hesapla — elle toplama/çıkarma bug'larını önler
    private void recalculateTotalPrice(Basket basket) {
        BigDecimal total = basket.getItems().stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        basket.setTotalPrice(total);
    }
}