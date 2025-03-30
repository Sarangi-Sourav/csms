package com.zynetic.ev_charger_management.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zynetic.ev_charger_management.dto.ChargeResponseDetails;
import com.zynetic.ev_charger_management.dto.ChargeTransactionDTO;
import com.zynetic.ev_charger_management.dto.ChargeTransactionResponse;
import com.zynetic.ev_charger_management.entity.ChargeTransactionEntity;
import com.zynetic.ev_charger_management.repository.ChargerTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class ChargerTransactionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChargerTransactionService.class);

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private final Validator validator;

    private final ChargerTransactionRepository chargerTransactionRepository;

    public ChargerTransactionService(Validator validator, ChargerTransactionRepository chargerTransactionRepository) {
        this.validator = validator;
        this.chargerTransactionRepository = chargerTransactionRepository;
    }

    public void processRemoteStartTransaction(WebSocketSession session, String uniqueId, JsonNode payloadNode) {
        LOGGER.info("Inside the chargerTransactionService.processRemoteStartTransaction method ...");
        try {
            ChargeTransactionDTO requestPayload = objectMapper.treeToValue(payloadNode, ChargeTransactionDTO.class);
            String chargerId = (String) session.getAttributes().get("chargerId");
            // Validate request payload
            Errors validationErrors = new BeanPropertyBindingResult(requestPayload, ChargeTransactionDTO.class.getName());
            validator.validate(requestPayload, validationErrors);

            if (validationErrors.hasErrors()) {
                Map<String, String> errors = validationErrors.getFieldErrors().stream().collect(Collectors.toMap(errorObject -> errorObject.getField(), ObjectError::getDefaultMessage));
                String errorMessage = errors.values().stream().toList().toString();
                sendErrorResponse(session, uniqueId, "ValidationError", errorMessage);
                LOGGER.info("Validation failed for the required fields with error message: {}", errorMessage);
                return;
            }
            // Send success response
            String response = String.format("[3, \"%s\", {\"status\": \"Accepted\", \"currentTime\": \"%s\"}]",
                    uniqueId, Instant.now().toString());
            session.sendMessage(new TextMessage(response));

            // Persist transaction info | should be handled separately from the response
            ChargeTransactionEntity transaction = new ChargeTransactionEntity();
            transaction.setIdTag(requestPayload.getIdTag());
            transaction.setConnectorId(requestPayload.getConnectorId());
            transaction.setChargingProfile(requestPayload.getChargingProfile());
            transaction.setCreatedAt(LocalDateTime.now());
            transaction.setChargerId(chargerId);
            chargerTransactionRepository.save(transaction);


        } catch (Exception ex) {
            sendErrorResponse(session, uniqueId, "InternalError", ex.getMessage());
            LOGGER.info("Exception occurred during transaction processing: {}", String.valueOf(ex));
        }
    }

    private void sendErrorResponse(WebSocketSession session, String uniqueId, String errorCode, String errorMessage) {
        try {
            String errorResponse = String.format("[4, \"%s\", \"%s\", \"%s\", {}]", uniqueId, errorCode, errorMessage);
            session.sendMessage(new TextMessage(errorResponse));
        } catch (Exception e) {
            LOGGER.error("Exception occurred during sending the errorResponse for Transaction failure: {}", String.valueOf(e));
        }
    }

    public ChargeTransactionResponse getByIdTag(String idTag){
        ChargeTransactionResponse response = new ChargeTransactionResponse();
        Optional<List<ChargeTransactionEntity>> transactionEntities = chargerTransactionRepository.findByIdTag(idTag);
        transactionEntities.ifPresent(
                chargeTransactionEntities -> setTheChargeTransactionResponseObject(
                        response, chargeTransactionEntities));
        return response;
    }

    public ChargeTransactionResponse getAllTransaction(){
        ChargeTransactionResponse response = new ChargeTransactionResponse();
        List<ChargeTransactionEntity> transactionEntities = chargerTransactionRepository.findAll();
        setTheChargeTransactionResponseObject(response, transactionEntities);
        return response;
    }

    private void setTheChargeTransactionResponseObject(ChargeTransactionResponse response, List<ChargeTransactionEntity> transactionEntities){
        if (!transactionEntities.isEmpty()) {
            List<ChargeResponseDetails> chargeResponseDetails = transactionEntities.stream()
                    .map(ChargeResponseDetails::new)
                    .collect(Collectors.toList());

            response.setTransactionDetails(chargeResponseDetails);
        } else LOGGER.error("No Transaction info found...");
    }

    @Transactional(readOnly = true)
    public ChargeTransactionResponse searchTransactions(String startTime, String endTime) {
        ChargeTransactionResponse response = new ChargeTransactionResponse();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        try {
            LocalDateTime formattedStartDate = (startTime == null || startTime.isEmpty())
                    ? LocalDateTime.now()
                    : LocalDate.parse(startTime, formatter).atStartOfDay();

            LocalDateTime formattedEndDate = (endTime == null || endTime.isEmpty())
                    ? LocalDateTime.now()
                    : LocalDate.parse(endTime, formatter).atTime(23, 59, 59); // for end of the day

            if (formattedStartDate.isAfter(formattedEndDate)) {
                throw new IllegalArgumentException("startTime must be before endTime.");
            }
            List<ChargeTransactionEntity> transactionEntities =
                    chargerTransactionRepository.findTransactionsByTimeRange(formattedStartDate, formattedEndDate);

            setTheChargeTransactionResponseObject(response, transactionEntities);

        } catch (DateTimeParseException e) {
            LOGGER.error("Exception occurred while parsing input date: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid date format. Please use MM/dd/yyyy.");
        }

        return response;
    }

    public ChargeTransactionResponse getByChargeId(String chargerId) {
        ChargeTransactionResponse response = new ChargeTransactionResponse();
        Optional<List<ChargeTransactionEntity>> transactionEntities = chargerTransactionRepository.findByChargerId(chargerId);
        transactionEntities.ifPresent(
                chargeTransactionEntities -> setTheChargeTransactionResponseObject(
                        response, chargeTransactionEntities));
        return response;
    }
}
