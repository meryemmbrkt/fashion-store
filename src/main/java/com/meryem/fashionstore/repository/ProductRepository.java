package com.meryem.fashionstore.repository;

import com.meryem.fashionstore.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndIsActiveTrue(Long id);

    List<Product> findByStoreIdAndIsActiveTrue(Long storeId);

    List<Product> findByCategoryIdAndIsActiveTrue(Long categoryId);

    List<Product> findAllByIsActiveTrue();

    Page<Product> findAllByIsActiveTrue(Pageable pageable);

    List<Product> findByStoreIdAndCategoryIdAndIsActiveTrue(
            Long storeId,
            Long categoryId
    );

    boolean existsByCategoryIdAndIsActiveTrue(Long categoryId);

    @Query("""
        SELECT p
        FROM Product p
        WHERE p.isActive = true
        AND LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    List<Product> searchByName(@Param("query") String query);
}