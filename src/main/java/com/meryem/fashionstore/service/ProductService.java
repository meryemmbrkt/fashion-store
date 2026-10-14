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
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
public class ProductService {
    private static final String PRODUCT_CACHE = "products";
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final BasketItemRepository basketItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductMapper productMapper;
    private final SecurityUtil securityUtil;
    private final ProductLogService productLogService;
    private final CacheManager cacheManager;
    private final RedisTemplate<String, ProductResponse> productRedisTemplate;


    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        log.info("GET ALL PRODUCTS - Checking Redis");
        List<ProductResponse> productsFromRedis = new ArrayList<>();
        ScanOptions scanOptions = ScanOptions.scanOptions()
                        .match("products::*")
                        .count(1000)
                        .build();
        try (Cursor<String> cursor = productRedisTemplate.scan(scanOptions)) {
            while (cursor.hasNext()) {
                String key = cursor.next();
                ProductResponse product = productRedisTemplate.opsForValue().get(key);
                if (product != null) {productsFromRedis.add(product);}}
        }
        if (!productsFromRedis.isEmpty()) {productsFromRedis.sort(Comparator.comparing(ProductResponse::id));
            log.info("GET ALL PRODUCTS - {} products loaded from REDIS", productsFromRedis.size());
            return productsFromRedis;}
        log.info("GET ALL PRODUCTS - Redis empty, reading from DATABASE");
        List<ProductResponse> products = productRepository
                        .findAllByIsActiveTrue()
                        .stream()
                        .map(productMapper::toResponse)
                        .toList();
        Cache productCache = cacheManager.getCache(PRODUCT_CACHE);
        if (productCache != null) {
            for (ProductResponse product : products) {productCache.put(product.id(), product);
            }
        }log.info("GET ALL PRODUCTS - {} products loaded from DATABASE and individually cached", products.size());
        return products;}

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<ProductResponse>
    getAllProductsPaged(int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by("price").ascending());
        return productRepository.findAllByIsActiveTrue(pageable).map(productMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByStore(Long storeId) {
        return productRepository
                .findByStoreIdAndIsActiveTrue(storeId)
                .stream()
                .map(productMapper::toResponse)
                .toList();}
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        return productRepository
                .findByCategoryIdAndIsActiveTrue(categoryId)
                .stream()
                .map(productMapper::toResponse)
                .toList();}
    @Transactional(readOnly = true)
    @Cacheable(value = PRODUCT_CACHE, key = "#id")
    public ProductResponse getProductById(Long id) {
        log.info("PRODUCT CACHE MISS - Redis'te ürün bulunamadı, DB'ye gidiliyor: id={}", id);
        Product product = findActiveProductOrThrow(id, "Aradığınız ürün sistemde bulunamadı!");
        log.info("PRODUCT LOADED FROM DATABASE - id={}, name={}", product.getId(), product.getName());
        return productMapper.toResponse(product);}


    @Transactional
    @CacheEvict(value = PRODUCT_CACHE, allEntries = true)
    public ProductResponse addProductToStoreAndCategory(
            Long storeId,
            Long categoryId,
            ProductRequest request) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            log.error("STORE NOT FOUND - storeId={}", storeId);
            return new ResourceNotFoundException("Aradığınız mağaza sistemde bulunamadı!");});
        securityUtil.checkStoreOwnership(store);
        Category category = categoryRepository.findByIdAndStoreIdAndIsActiveTrue(categoryId, storeId).orElseThrow(() -> {
            log.error("CATEGORY NOT FOUND - categoryId={}, storeId={}", categoryId, storeId);
            return new ResourceNotFoundException("Kategori bu mağazaya ait değil veya aktif değil!");});
        Product product = productMapper.toEntity(request, store, category);
        product.setIsActive(true);
        Product savedProduct = productRepository.save(product);
        logProductAction(savedProduct, "CREATED", "Fiyat: " + savedProduct.getPrice() + ", Stok: " + savedProduct.getStock());
        log.info("PRODUCT CREATED - id={}, name={}, storeId={}", savedProduct.getId(), savedProduct.getName(), storeId);
        return productMapper.toResponse(savedProduct);
    }


    @Transactional
    @Caching(put = {@CachePut(value = PRODUCT_CACHE, key = "#id")})
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("PRODUCT UPDATE - id={}", id);
        Product product = findActiveProductOrThrow(id, "Güncellenmek istenen ürün bulunamadı!");
        securityUtil.checkStoreOwnership(product.getStore());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        Product updatedProduct = productRepository.save(product);
        logProductAction(updatedProduct, "UPDATED", "Yeni fiyat: " + updatedProduct.getPrice());
        log.info("PRODUCT UPDATED - id={}, name={}, price={}, stock={}",
                updatedProduct.getId(),
                updatedProduct.getName(),
                updatedProduct.getPrice(),
                updatedProduct.getStock());
        return productMapper.toResponse(updatedProduct);}

    /*
    //mevcut delete akisi safe delete işlemi uygulamaktadır.Bunu dbyide etkileyecek hale getirelim.ilgili product dbden tamamen silinsin bu metod çalışınca
    // bir koşul koyalım buraya. eğer ürün aktif bir ürün ise warn seviyesinde log gönderelim ve bir exception fırlatalım.

    @Transactional
    @Caching(evict = {@CacheEvict(value = PRODUCT_CACHE, key = "#id")})
    public void deleteProduct(Long id) {
        log.info("PRODUCT DELETE - id={}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Silinmek istenen ürün bulunamadı!"));
        securityUtil.checkStoreOwnership(product.getStore());
        if (Boolean.TRUE.equals(product.getIsActive())) {
            log.warn("ÜRÜN SİLME ENGELLENDİ (ÜRÜN AKTİF) - id={}", id);
            throw new IllegalArgumentException("Aktif ürün silinemez! Önce ürünü pasif hale getirin.");
        }
        logProductAction(product, "DELETED", "URUN DB DEN SILINDI silindi");
        productRepository.delete(product);
        log.info("URUN TAMAMEN SİLİNDİ - id={}, ad={}", id, product.getName());
    } */

    @Transactional
    @Caching(evict = {@CacheEvict(value = PRODUCT_CACHE, key = "#id")})
    public void deleteProduct(Long id) {
        log.info("PRODUCT DELETE - id={}", id);
        Product product = findActiveProductOrThrow(id, "Silinmek istenen ürün bulunamadı!");
        securityUtil.checkStoreOwnership(product.getStore());
        if (orderItemRepository.existsByProductId(id)) {
            log.warn("PRODUCT DELETE BLOCKED - id={}", id);
            throw new IllegalArgumentException("Bu ürün geçmiş siparişlerde yer aldığı için silinemez!");}
        if (basketItemRepository.existsByProductId(id)) {
            basketItemRepository.deleteByProductId(id);}
        product.setIsActive(false);
        productRepository.save(product);
        logProductAction(product, "DELETED", "Ürün soft delete yapıldı");
        log.info("PRODUCT DELETED - id={}, name={}", id, product.getName());
    }

    @Transactional
    @CacheEvict(
            value = PRODUCT_CACHE,
            key = "#productId")
    public void decreaseStock(
            Long productId,
            Integer quantity
    ) {log.info("PRODUCT STOCK UPDATE - productId={}, quantity={}",
                productId,
                quantity
        );Product product = findActiveProductOrThrow(productId, "Ürün bulunamadı!");
        if (product.getStock() < quantity) {log.warn("INSUFFICIENT STOCK - productId={}, requested={}, available={}",
                    productId,
                    quantity,
                    product.getStock());
            throw new IllegalArgumentException("Yetersiz stok!");}product.setStock(
                product.getStock() - quantity
        );
        productRepository.save(product);
        log.info(
                "PRODUCT STOCK UPDATED - productId={}, newStock={}",
                productId,
                product.getStock()
        );
    }
    private Product findActiveProductOrThrow(
            Long id,
            String message
    ) {return productRepository
                .findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> {log.error("PRODUCT NOT FOUND - id={}", id);
                    return new ResourceNotFoundException(message);});
    }
    private void logProductAction(
            Product product,
            String action,
            String detail
    ) {var currentUser = securityUtil.getCurrentUser();
        productLogService.log(
                product.getId(),
                product.getName(),
                action,
                currentUser.getId(),
                currentUser.getEmail(),
                detail
        );
    }
}
