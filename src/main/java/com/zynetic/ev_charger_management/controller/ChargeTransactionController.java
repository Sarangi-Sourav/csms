package com.zynetic.ev_charger_management.controller;

import com.zynetic.ev_charger_management.dto.ChargeTransactionDTO;
import com.zynetic.ev_charger_management.dto.ChargeTransactionResponse;
import com.zynetic.ev_charger_management.dto.TransactionSearchRequest;
import com.zynetic.ev_charger_management.service.ChargerTransactionService;
import org.hibernate.query.results.ResultSetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/transaction")
public class ChargeTransactionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChargeTransactionController.class);

    private final ChargerTransactionService chargerTransactionService;

    public ChargeTransactionController(ChargerTransactionService chargerTransactionService){
        this.chargerTransactionService = chargerTransactionService;
    }

    @GetMapping("/all")
    public ResponseEntity<ChargeTransactionResponse> getAllTransactions() throws Exception {
        ChargeTransactionResponse chargeTransactionResponse = null;
        chargeTransactionResponse = chargerTransactionService.getAllTransaction();
        return new ResponseEntity<>(chargeTransactionResponse, HttpStatus.OK);
    }

    @GetMapping("/logs-by-idtag")
    public ResponseEntity<ChargeTransactionResponse> getTransactions(@RequestParam String idTag) throws Exception {
        ChargeTransactionResponse chargeTransactionResponse = null;
        chargeTransactionResponse = chargerTransactionService.getByIdTag(idTag);
        return new ResponseEntity<>(chargeTransactionResponse, HttpStatus.OK);
    }

    @GetMapping("/logs-by-chargeid")
    public ResponseEntity<ChargeTransactionResponse> getTransactionsBy(@RequestParam String chargerId) throws Exception {
        ChargeTransactionResponse chargeTransactionResponse = null;
        chargeTransactionResponse = chargerTransactionService.getByChargeId(chargerId);
        return new ResponseEntity<>(chargeTransactionResponse, HttpStatus.OK);
    }

    @PostMapping("/logs-by-time")
    public ResponseEntity<ChargeTransactionResponse>  searchTransactions(@RequestBody TransactionSearchRequest request) {
        ChargeTransactionResponse chargeTransactionResponse = null;
        chargeTransactionResponse = chargerTransactionService.searchTransactions(request.getStartTime(), request.getEndTime());
        return new ResponseEntity<>(chargeTransactionResponse, HttpStatus.OK);
    }
}
