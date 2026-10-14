package com.meryem.fashionstore.service;
import com.meryem.fashionstore.dto.CategoryRequest;
import com.meryem.fashionstore.dto.CategoryResponse;
import com.meryem.fashionstore.entity.Category;
import com.meryem.fashionstore.entity.Store;
import com.meryem.fashionstore.exception.ResourceNotFoundException;
import com.meryem.fashionstore.mapper.CategoryMapper;
import com.meryem.fashionstore.repository.CategoryRepository;
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
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;
    private final CategoryMapper categoryMapper;
    private final ProductRepository productRepository;
    private final SecurityUtil securityUtil;
    private final CacheManager cacheManager;
    private final RedisTemplate<String, CategoryResponse> categoryRedisTemplate;

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse createCategory(Long storeId, CategoryRequest request) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            log.error("STORE NOT FOUND - category create: storeId={}", storeId);
            return new ResourceNotFoundException("Kategori eklemek istediğiniz mağaza bulunamadı!");});

        securityUtil.checkStoreOwnership(store);
        Category category = categoryMapper.toEntity(request, store);

        category.setIsActive(true);

        Category savedCategory = categoryRepository.save(category);
        log.info("CATEGORY CREATED - id={}, name={}, storeId={}", savedCategory.getId(), savedCategory.getName(), storeId);
        return categoryMapper.toResponse(savedCategory);}

    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#id")
    public CategoryResponse getCategoryById(Long id) {
        log.info("CATEGORY CACHE MISS - Redis'te kategori yok, DB'ye gidiliyor: id={}", id);
        Category category = categoryRepository.findByIdAndIsActiveTrue(id).orElseThrow(() -> {log.error("CATEGORY NOT FOUND - id={}", id);
            return new ResourceNotFoundException("Aradığınız kategori sistemde bulunamadı!");});

        log.info("CATEGORY DB LOAD - id={}, name={}", category.getId(), category.getName());
        return categoryMapper.toResponse(category);}

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesByStore(Long storeId) {
        log.info("GET CATEGORIES BY STORE - storeId={}", storeId);
        return categoryRepository.findByStoreIdAndIsActiveTrue(storeId).stream().map(categoryMapper::toResponse).toList();}

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        log.info("GET ALL CATEGORIES - Checking Redis");
        List<CategoryResponse> categoriesFromRedis = new ArrayList<>();
        ScanOptions scanOptions = ScanOptions.scanOptions().match("categories::*").count(1000).build();
        try (Cursor<String> cursor = categoryRedisTemplate.scan(scanOptions)) {
            while (cursor.hasNext()) {
                String key = cursor.next();
                CategoryResponse category = categoryRedisTemplate.opsForValue().get(key);
                if (category != null) {categoriesFromRedis.add(category);}}}
        if (!categoriesFromRedis.isEmpty()) {categoriesFromRedis.sort(Comparator.comparing(CategoryResponse::id));
            log.info("GET ALL CATEGORIES - {} categories loaded from REDIS", categoriesFromRedis.size());
            return categoriesFromRedis;}

        log.info("GET ALL CATEGORIES - Redis empty, reading from DATABASE");
        List<CategoryResponse> categories = categoryRepository.findAllByIsActiveTrue().stream().map(categoryMapper::toResponse).toList();
        Cache categoryCache = cacheManager.getCache("categories");
        if (categoryCache != null) {for (CategoryResponse category : categories) {categoryCache.put(category.id(), category);}}
        log.info("GET ALL CATEGORIES - {} categories loaded from DATABASE and individually cached", categories.size());
        return categories;}

    @Transactional
    @Caching(put = {@CachePut(value = "categories", key = "#id")})
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findByIdAndIsActiveTrue(id).orElseThrow(() -> {
            log.error("CATEGORY NOT FOUND - update: id={}", id);return new ResourceNotFoundException("Güncellenmek istenen kategori bulunamadı!");});
        securityUtil.checkStoreOwnership(category.getStore());
        category.setName(request.name());
        Category updatedCategory = categoryRepository.save(category);
        log.info("CATEGORY UPDATED - id={}, name={}", updatedCategory.getId(), updatedCategory.getName());
        return categoryMapper.toResponse(updatedCategory);}

    @Transactional
    @Caching(evict = {@CacheEvict(value = "categories", key = "#id")})
    public void deleteCategory(Long id) {Category category = categoryRepository.findByIdAndIsActiveTrue(id).orElseThrow(() -> {log.error("CATEGORY NOT FOUND - delete: id={}", id);
            return new ResourceNotFoundException("Silmek istediğiniz kategori bulunamadı!");});
        securityUtil.checkStoreOwnership(category.getStore());
        if (productRepository.existsByCategoryIdAndIsActiveTrue(id)) {
            log.warn("CATEGORY DELETE BLOCKED - categoryId={}", id);
            throw new IllegalArgumentException("Bu kategoride ürünler var! Önce ürünleri silin veya başka kategoriye taşıyın.");}
        category.setIsActive(false);
        categoryRepository.save(category);
        log.info("CATEGORY SOFT DELETED - id={}, name={}", category.getId(), category.getName());

    }
}
