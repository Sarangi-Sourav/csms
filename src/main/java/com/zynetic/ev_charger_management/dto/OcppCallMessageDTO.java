package com.zynetic.ev_charger_management.dto;

import com.fasterxml.jackson.databind.JsonNode;
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
public class OcppCallMessageDTO {
    @NotNull(message = "MessageTypeId is required")
    private Integer messageTypeId; // e.g., 2 for CALL

    @NotBlank(message = "UniqueId is required")
    private String uniqueId; // unique per call

    @NotBlank(message = "Action is required")
    private String action; // e.g., "RemoteStartTransactionRequest"

    @NotNull(message = "Payload is required")
    private JsonNode payload; // generic; will be parsed into a specific DTO based on the action

}
