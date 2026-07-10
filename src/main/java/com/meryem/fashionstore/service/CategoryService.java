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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;
    private final CategoryMapper categoryMapper;
    private final ProductRepository productRepository;
    private final SecurityUtil securityUtil;

    @Transactional
    public CategoryResponse createCategory(Long storeId, CategoryRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Kategori eklemek istediğiniz mağaza bulunamadı!"
                ));

        securityUtil.checkStoreOwnership(store);

        Category category = categoryMapper.toEntity(request);
        category.setStore(store);

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesByStore(Long storeId) {
        List<Category> categories = categoryRepository.findByStoreId(storeId);
        return categories.stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Silmek istediğiniz kategori bulunamadı!"
                ));

        securityUtil.checkStoreOwnership(category.getStore());

        if (productRepository.existsByCategoryId(id)) {
            throw new IllegalArgumentException(
                    "Bu kategoride ürünler var! Önce ürünleri silin veya başka kategoriye taşıyın."
            );
        }

        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::toResponse)
                .toList();
    }
}