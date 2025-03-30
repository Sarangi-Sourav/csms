package com.zynetic.ev_charger_management.repository;

import com.zynetic.ev_charger_management.entity.ChargePointEntity;
import com.zynetic.ev_charger_management.entity.ChargerStatusEnum;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;

public interface ChargerRepository extends JpaRepository<ChargePointEntity, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE ChargePointEntity c SET c.lastHeartbeat = :timestamp, c.status = 'Available' WHERE c.chargerId = :chargerId AND c.status = 'Unavailable'")
    void updateHeartbeat(@Param("chargerId") String chargerId, @Param("timestamp") Timestamp timestamp);

    @Modifying
    @Transactional
    @Query("UPDATE ChargePointEntity c SET c.status = 'Unavailable' WHERE c.lastHeartbeat < :thresholdTime AND c.status != 'Unavailable'")
    int markChargersUnavailable(@Param("thresholdTime") Timestamp thresholdTime);

    @Modifying
    @Transactional
    @Query("UPDATE ChargePointEntity c SET c.status = :status WHERE c.chargerId = :chargerId")
    void updateChargerStatus(String chargerId, ChargerStatusEnum status);
}
