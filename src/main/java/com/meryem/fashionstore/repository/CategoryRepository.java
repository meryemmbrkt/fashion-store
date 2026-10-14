package com.meryem.fashionstore.repository;

import com.meryem.fashionstore.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndIsActiveTrue(Long id);

    Optional<Category> findByIdAndStoreIdAndIsActiveTrue(Long id, Long storeId);

    List<Category> findByStoreIdAndIsActiveTrue(Long storeId);

    List<Category> findAllByIsActiveTrue();

    boolean existsByStoreIdAndIsActiveTrue(Long storeId);
}