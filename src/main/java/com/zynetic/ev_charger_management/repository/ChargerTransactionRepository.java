package com.zynetic.ev_charger_management.repository;

import com.zynetic.ev_charger_management.entity.ChargePointEntity;
import com.zynetic.ev_charger_management.entity.ChargeTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ChargerTransactionRepository extends JpaRepository<ChargeTransactionEntity, Integer> {
    public Optional<List<ChargeTransactionEntity>> findByIdTag(String idTag);

    public Optional<List<ChargeTransactionEntity>> findByChargerId(String chargerId);

    @Query("SELECT ct FROM ChargeTransactionEntity ct WHERE ct.createdAt BETWEEN :startTime AND :endTime")
    List<ChargeTransactionEntity> findTransactionsByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
