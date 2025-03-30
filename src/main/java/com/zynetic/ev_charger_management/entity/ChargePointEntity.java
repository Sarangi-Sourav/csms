package com.zynetic.ev_charger_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "charger")
public class ChargePointEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    private String chargerId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChargerStatusEnum status;
    private Timestamp lastHeartbeat;
    private Timestamp createdAt;
    private Timestamp updatedAt;

}
