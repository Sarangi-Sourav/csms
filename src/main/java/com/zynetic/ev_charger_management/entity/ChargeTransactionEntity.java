package com.zynetic.ev_charger_management.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zynetic.ev_charger_management.dto.ChargingProfileDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "charge_transaction")
public class ChargeTransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String idTag;
    private Integer connectorId;
    @Column(columnDefinition = "JSON")
    @Setter(AccessLevel.NONE)
    private String chargingProfile;
    private LocalDateTime createdAt;
    private String chargerId;

    // Convert DTO to JSON String before persisting
    public void setChargingProfile(ChargingProfileDTO chargingProfileDTO) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        this.chargingProfile = objectMapper.writeValueAsString(chargingProfileDTO);
    }

}
