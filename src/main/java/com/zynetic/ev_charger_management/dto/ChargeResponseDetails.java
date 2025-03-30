package com.zynetic.ev_charger_management.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zynetic.ev_charger_management.entity.ChargeTransactionEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChargeResponseDetails {

    private Integer id;
    private String idTag;
    private Integer connectorId;
    private ChargingProfileDTO chargingProfile;
    private LocalDateTime createdAt;
    private String chargerId;

    public ChargeResponseDetails(ChargeTransactionEntity entity) {
        this.id = entity.getId();
        this.idTag = entity.getIdTag();
        this.connectorId = entity.getConnectorId();
        this.createdAt = entity.getCreatedAt();
        this.chargerId = entity.getChargerId();

        if (entity.getChargingProfile() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                this.chargingProfile = objectMapper.readValue(entity.getChargingProfile(), ChargingProfileDTO.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error while parsing ChargingProfile JSON", e);
            }
        }
    }
}

