package com.saf.restaurant.repository;

import com.saf.restaurant.entity.ReceiptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<ReceiptEntity, Long> {
    Optional<ReceiptEntity> findByOrderId(String orderId);
    List<ReceiptEntity> findAllByOrderByIssuedAtDesc();
}
