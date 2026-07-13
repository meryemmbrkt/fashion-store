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
import com.meryem.fashionstore.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final BasketItemRepository basketItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final SecurityUtil securityUtil;

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByStore(Long storeId) {
        return productRepository.findByStoreId(storeId).stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProductsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("price").ascending());
        return productRepository.findAll(pageable).map(productMapper::toResponse);
    }

    @Transactional
    public ProductResponse addProductToStoreAndCategory(Long storeId, Long categoryId, ProductRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> {
                    log.error("Store not found! storeId={}", storeId);
                    return new ResourceNotFoundException("Aradığınız mağaza sistemde bulunamadı!");
                });

        securityUtil.checkStoreOwnership(store);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("Category not found! categoryId={}, storeId={}", categoryId, storeId);
                    return new ResourceNotFoundException("Aradığınız kategori bu mağazada bulunamadı!");
                });

        Product product = productMapper.toEntity(request, store, category);

        Product savedProduct = productRepository.save(product);
        log.info("Product created: id={}, name={}, storeId={}",
                savedProduct.getId(), savedProduct.getName(), storeId);

        return productMapper.toResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found! productId={}", id);
                    return new ResourceNotFoundException("Aradığınız ürün sistemde bulunamadı!");
                });
        return productMapper.toResponse(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product to delete not found! productId={}", id);
                    return new ResourceNotFoundException("Silmek istediğiniz ürün bulunamadı!");
                });

        securityUtil.checkStoreOwnership(product.getStore());

        if (orderItemRepository.existsByProductId(id)) {
            log.warn("Delete blocked, product exists in past orders: productId={}", id);
            throw new IllegalArgumentException(
                    "Bu ürün geçmiş siparişlerde yer aldığı için silinemez!"
            );
        }

        if (basketItemRepository.existsByProductId(id)) {
            log.info("Removing product from active baskets before delete: productId={}", id);
            basketItemRepository.deleteByProductId(id);
        }

        productRepository.delete(product);
        log.info("Product deleted: id={}, name={}", id, product.getName());
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product to update not found! productId={}", id);
                    return new ResourceNotFoundException("Güncellenmek istenen ürün bulunamadı!");
                });

        securityUtil.checkStoreOwnership(product.getStore());

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated: id={}, name={}", id, updatedProduct.getName());
        return productMapper.toResponse(updatedProduct);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return products.stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional
    public void decreaseStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found for stock decrease! productId={}", productId);
                    return new ResourceNotFoundException("Ürün bulunamadı!");
                });

        if (product.getStock() < quantity) {
            log.warn("Insufficient stock: productId={}, requested={}, available={}",
                    productId, quantity, product.getStock());
            throw new IllegalArgumentException(
                    "Yetersiz stok! Ürün: " + product.getName() + ", Mevcut: " + product.getStock()
            );
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
        log.info("Stock decreased: productId={}, quantity={}, newStock={}",
                productId, quantity, product.getStock());
    }
}