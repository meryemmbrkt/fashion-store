package com.meryem.fashionstore.repository;

import com.meryem.fashionstore.entity.Order;
import com.meryem.fashionstore.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // En yeni siparişler önce
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    // Belirli statüye ait siparişleri bul
    List<Order> findByStatusOrderByOrderDateDesc(OrderStatus status);
}