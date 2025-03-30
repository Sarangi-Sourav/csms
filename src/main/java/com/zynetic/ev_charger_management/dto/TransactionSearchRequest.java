package com.zynetic.ev_charger_management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSearchRequest {
    @JsonFormat(pattern = "MM/dd/yyyy")
    private String startTime;
    @JsonFormat(pattern ="MM/dd/yyyy")
    private String endTime;
}