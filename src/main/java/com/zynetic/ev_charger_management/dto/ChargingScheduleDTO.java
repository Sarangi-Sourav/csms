package com.zynetic.ev_charger_management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChargingScheduleDTO {
    // duration is optional
    private Integer duration;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime startSchedule;

    @NotBlank(message = "chargingRateUnit is required")
    @Pattern(regexp = "A|W", message = "chargingRateUnit must be A or W")
    private String chargingRateUnit;

    @NotNull(message = "chargingSchedulePeriod is required")
    @Valid
    private List<ChargingSchedulePeriodDTO> chargingSchedulePeriod;

    // minChargingRate is optional
    private Double minChargingRate;
}
