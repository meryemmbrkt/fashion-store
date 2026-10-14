package com.meryem.fashionstore.repository;
import com.meryem.fashionstore.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    Optional<Store> findByIdAndActiveTrue(Long id);

    Optional<Store> findByUserIdAndActiveTrue(Long userId);

    List<Store> findAllByActiveTrue();
}
