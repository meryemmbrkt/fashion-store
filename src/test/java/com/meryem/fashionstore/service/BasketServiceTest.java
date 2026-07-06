package com.meryem.fashionstore.service;

import com.meryem.fashionstore.dto.BasketResponse;
import com.meryem.fashionstore.entity.Basket;
import com.meryem.fashionstore.entity.BasketItem;
import com.meryem.fashionstore.entity.Product;
import com.meryem.fashionstore.entity.User;
import com.meryem.fashionstore.exception.ResourceNotFoundException;
import com.meryem.fashionstore.mapper.BasketMapper;
import com.meryem.fashionstore.repository.BasketRepository;
import com.meryem.fashionstore.repository.ProductRepository;
import com.meryem.fashionstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasketServiceTest {

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BasketMapper basketMapper;

    @InjectMocks
    private BasketService basketService;

    private User user;
    private Product product;
    private Basket basket;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("Test");
        user.setEmail("test@test.com");

        product = new Product();
        product.setId(10L);
        product.setName("Test Ürün");
        product.setPrice(BigDecimal.valueOf(100));
        product.setStock(5);

        basket = new Basket();
        basket.setId(1L);
        basket.setUser(user);
        basket.setTotalPrice(BigDecimal.ZERO);
    }

    @Test
    void addProductToBasket_stokYeterliyse_urunuEkler() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(basketRepository.findByUserId(1L)).thenReturn(Optional.of(basket));
        when(basketRepository.save(any(Basket.class))).thenReturn(basket);
        when(basketMapper.toResponse(any(Basket.class)))
                .thenReturn(new BasketResponse(1L, 1L, BigDecimal.valueOf(200), null));

        // when
        BasketResponse response = basketService.addProductToBasket(1L, 10L, 2);

        // then
        assertNotNull(response);
        assertEquals(1, basket.getItems().size());
        assertEquals(2, basket.getItems().get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(200), basket.getTotalPrice()); // 2 x 100
        verify(basketRepository).save(basket);
    }

    @Test
    void addProductToBasket_stokYetersizse_hataFirlatir() {
        // given
        product.setStock(1); // stokta 1 var, 5 isteyeceğiz
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        // when + then
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> basketService.addProductToBasket(1L, 10L, 5)
        );
        assertTrue(ex.getMessage().contains("Stokta yeterli ürün yok"));
        verify(basketRepository, never()).save(any()); // hiçbir şey kaydedilmemeli
    }

    @Test
    void addProductToBasket_kullaniciYoksa_hataFirlatir() {
        // given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // when + then
        assertThrows(
                ResourceNotFoundException.class,
                () -> basketService.addProductToBasket(99L, 10L, 1)
        );
    }

    @Test
    void addProductToBasket_urunZatenSepetteyse_adediniArtirir() {
        // given: sepette bu üründen 1 tane zaten var
        BasketItem existingItem = new BasketItem();
        existingItem.setProduct(product);
        existingItem.setQuantity(1);
        existingItem.setBasket(basket);
        basket.getItems().add(existingItem);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(basketRepository.findByUserId(1L)).thenReturn(Optional.of(basket));
        when(basketRepository.save(any(Basket.class))).thenReturn(basket);
        when(basketMapper.toResponse(any(Basket.class)))
                .thenReturn(new BasketResponse(1L, 1L, BigDecimal.valueOf(300), null));

        // when
        basketService.addProductToBasket(1L, 10L, 2);

        // then: yeni satır AÇILMAMALI, mevcut satırın adedi 3 olmalı
        assertEquals(1, basket.getItems().size());
        assertEquals(3, basket.getItems().get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(300), basket.getTotalPrice()); // 3 x 100
    }
}