package com.zynetic.ev_charger_management.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChargeTransactionDTO {
    @NotNull(message = "connectorId is required")
    private Integer connectorId;

    @NotBlank(message = "idTag is required")
    @Size(max = 20, message = "idTag can be at most 20 characters")
    private String idTag;

    @NotNull(message = "chargingProfile is required")
    @Valid
    private ChargingProfileDTO chargingProfile;
}
