package com.zynetic.ev_charger_management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChargingProfileDTO {
    @NotNull(message = "chargingProfileId is required")
    private Integer chargingProfileId;

    // transactionId is optional
    private Integer transactionId;

    @NotNull(message = "stackLevel is required")
    private Integer stackLevel;

    @NotBlank(message = "chargingProfilePurpose is required")
    @Pattern(regexp = "ChargePointMaxProfile|TxDefaultProfile|TxProfile",
            message = "Invalid chargingProfilePurpose")
    private String chargingProfilePurpose;

    @NotBlank(message = "chargingProfileKind is required")
    @Pattern(regexp = "Absolute|Recurring|Relative",
            message = "Invalid chargingProfileKind")
    private String chargingProfileKind;

    // recurrencyKind is optional, if provided, it must be Daily or Weekly
    @Pattern(regexp = "Daily|Weekly", message = "Invalid recurrencyKind")
    private String recurrencyKind;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime validFrom;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime validTo;

    @NotNull(message = "chargingSchedule is required")
    @Valid
    private ChargingScheduleDTO chargingSchedule;
}
