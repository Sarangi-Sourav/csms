package com.zynetic.ev_charger_management.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zynetic.ev_charger_management.constants.OcppSpecificationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;

@Service
public class BootNotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BootNotificationService.class);

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    public void processBootNotification(WebSocketSession session, String uniqueId, JsonNode payloadNode) {
        if (!payloadNode.has("chargePointVendor") ||
                !payloadNode.has("chargePointModel") ||
                payloadNode.get("chargePointVendor").asText().trim().isEmpty() ||
                payloadNode.get("chargePointModel").asText().trim().isEmpty()) {
            sendErrorResponse(session, uniqueId, OcppSpecificationConstants.ERROR_PROTOCOL_ERROR, OcppSpecificationConstants.MESSAGE_PROTOCOL_ERROR);
            LOGGER.error("Missing required fields in BootNotification request!");
            return;
        }
        try {
            // Send success response
            String response = String.format("[3, \"%s\", {\"status\": \"Accepted\", \"currentTime\": \"%s\",\"interval\": 300}]",
                    uniqueId, Instant.now().toString());
            session.sendMessage(new TextMessage(response));
            LOGGER.info("Sent BootNotificationResponse: {}", response);
        } catch (Exception ex) {
            sendErrorResponse(session, uniqueId, "InternalError", ex.getMessage());
            LOGGER.info("Exception occurred during responding to BootNotification: {}", String.valueOf(ex));
        }

    }

    private void sendErrorResponse(WebSocketSession session, String uniqueId, String errorCode, String errorMessage) {
        try {
            String errorResponse = String.format("[4, \"%s\", \"%s\", \"%s\", {}]", uniqueId, errorCode, errorMessage);
            session.sendMessage(new TextMessage(errorResponse));
        } catch (Exception e) {
            LOGGER.error("Exception occurred during sending the errorResponse: {}", String.valueOf(e));
        }
    }
}
