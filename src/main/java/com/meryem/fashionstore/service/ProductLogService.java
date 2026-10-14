package com.meryem.fashionstore.service;

import com.meryem.fashionstore.entity.ProductLog;
import com.meryem.fashionstore.repository.ProductLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductLogService {

    private final ProductLogRepository productLogRepository;

    public void log(Long productId, String productName, String action,
                    Long userId, String userEmail, String details) {
        ProductLog productLog = ProductLog.builder()
                .productId(productId)
                .productName(productName)
                .action(action)
                .userId(userId)
                .userEmail(userEmail)
                .timestamp(LocalDateTime.now())
                .details(details)
                .build();

        productLogRepository.save(productLog);
        log.info("Product log saved: productId={}, action={}, userId={}",
                productId, action, userId);
    }

    public List<ProductLog> getByProduct(Long productId) {
        return productLogRepository.findByProductId(productId);
    }

    public List<ProductLog> getByAction(String action) {
        return productLogRepository.findByAction(action);
    }

    public List<ProductLog> getByUser(Long userId) {
        return productLogRepository.findByUserId(userId);
    }
}