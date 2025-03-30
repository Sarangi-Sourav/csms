package com.zynetic.ev_charger_management.dto;

import com.zynetic.ev_charger_management.entity.ChargeTransactionEntity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChargeTransactionResponse {
    private List<ChargeResponseDetails> transactionDetails;
}
