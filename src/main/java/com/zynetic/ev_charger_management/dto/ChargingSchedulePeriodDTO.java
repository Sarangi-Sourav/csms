package com.zynetic.ev_charger_management.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChargingSchedulePeriodDTO {
    @NotNull(message = "startPeriod is required")
    private Integer startPeriod;

    @NotNull(message = "limit is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private Double limit;

    // numberPhases is optional
    private Integer numberPhases;
}
