package com.zynetic.ev_charger_management.repository;

import com.zynetic.ev_charger_management.entity.ChargerCredEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChargerCredRepository extends JpaRepository<ChargerCredEntity, Integer> {
    @Query(value = "SELECT passKey FROM ChargerCredEntity WHERE chargerId = :chargerId")
    Optional<String> findPassKeyByChargerId(String chargerId);
}
