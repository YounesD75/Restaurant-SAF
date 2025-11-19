package com.saf.restaurant.repository;

import com.saf.restaurant.entity.TreasuryEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface TreasuryRepository extends JpaRepository<TreasuryEntryEntity, Long> {

    Optional<TreasuryEntryEntity> findByOrderId(String orderId);

    @Query("select coalesce(sum(t.amount), 0) from TreasuryEntryEntity t")
    BigDecimal totalRevenue();

    @Query("select count(t) from TreasuryEntryEntity t")
    long ordersSettled();
}
