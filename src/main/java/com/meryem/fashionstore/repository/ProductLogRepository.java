package com.meryem.fashionstore.repository;

import com.meryem.fashionstore.entity.ProductLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductLogRepository extends MongoRepository<ProductLog, String> {


    List<ProductLog> findByProductId(Long productId);


    List<ProductLog> findByAction(String action);


    List<ProductLog> findByUserId(Long userId);


    List<ProductLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);


    List<ProductLog> findByProductIdAndAction(Long productId, String action);
}
