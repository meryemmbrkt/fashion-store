package com.meryem.fashionstore.service;

import com.meryem.fashionstore.dto.ProductRequest;
import com.meryem.fashionstore.dto.ProductResponse;
import com.meryem.fashionstore.entity.Category;
import com.meryem.fashionstore.entity.Product;
import com.meryem.fashionstore.entity.Store;
import com.meryem.fashionstore.exception.ResourceNotFoundException;
import com.meryem.fashionstore.mapper.ProductMapper;
import com.meryem.fashionstore.repository.BasketItemRepository;
import com.meryem.fashionstore.repository.CategoryRepository;
import com.meryem.fashionstore.repository.OrderItemRepository;
import com.meryem.fashionstore.repository.ProductRepository;
import com.meryem.fashionstore.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final BasketItemRepository basketItemRepository;
    private final OrderItemRepository orderItemRepository;

    public List<ProductResponse> getProductsByStore(Long storeId) {
        return productRepository.findByStoreId(storeId).stream()
                .map(productMapper::toResponse)
                .toList();
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .toList();
    }

    public Page<ProductResponse> getAllProductsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable).map(productMapper::toResponse);
    }

    @Transactional
    public ProductResponse addProductToStore(Long storeId, ProductRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Mağaza bulunamadı!"));

        Product product = productMapper.toEntity(request);
        product.setStore(store);

        return productMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse addProductToStoreAndCategory(Long storeId, Long categoryId, ProductRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aradığınız mağaza sistemde bulunamadı!"
                ));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aradığınız kategori bu mağazada bulunamadı!"
                ));

        Product product = productMapper.toEntity(request);
        product.setStore(store);
        product.setCategory(category);

        return productMapper.toResponse(productRepository.save(product));
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aradığınız ürün sistemde bulunamadı!"
                ));
        return productMapper.toResponse(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Silmek istediğiniz ürün bulunamadı!"
                ));

        // Sipariş geçmişinde varsa silinemez (geçmiş bozulmamalı)
        if (orderItemRepository.existsByProductId(id)) {
            throw new IllegalArgumentException(
                    "Bu ürün geçmiş siparişlerde yer aldığı için silinemez!"
            );
        }

        // Sepetlerde varsa önce sepetlerden çıkar
        if (basketItemRepository.existsByProductId(id)) {
            basketItemRepository.deleteByProductId(id);
        }

        productRepository.delete(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Güncellenmek istenen ürün bulunamadı!"
                ));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());

        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return products.stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional
    public void decreaseStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı!"));

        if (product.getStock() < quantity) {
            throw new IllegalArgumentException(
                    "Yetersiz stok! Ürün: " + product.getName() + ", Mevcut: " + product.getStock()
            );
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }
}