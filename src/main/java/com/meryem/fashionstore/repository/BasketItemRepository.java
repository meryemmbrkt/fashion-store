package com.meryem.fashionstore.repository;

import com.meryem.fashionstore.entity.BasketItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasketItemRepository extends JpaRepository<BasketItem, Long> {

    boolean existsByProductId(Long productId);

    void deleteByProductId(Long productId);
}