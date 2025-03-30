package com.zynetic.ev_charger_management.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.zynetic.ev_charger_management.entity.ChargerStatusEnum;
import com.zynetic.ev_charger_management.repository.ChargerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class ChargerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChargerService.class);

    private final ChargerRepository chargerRepository;

    public ChargerService(ChargerRepository chargerRepository) {
        this.chargerRepository = chargerRepository;
    }

    public void handleHeartbeat(WebSocketSession session, String uniqueId, JsonNode payload) {
        // getting the chargerId from the WebSocket session
        String chargerId = (String) session.getAttributes().get("chargerId");
        chargerRepository.updateHeartbeat(chargerId, Timestamp.from(Instant.now()));
        try{
            // Send success response
            String response = String.format("[3, \"%s\", {\"currentTime\": \"%s\"}]",
                    uniqueId, Instant.now().toString());
            session.sendMessage(new TextMessage(response));
            LOGGER.info("Heartbeat received from charger: {}", chargerId);
        }catch (Exception ex) {
            sendErrorResponse(session, uniqueId, "InternalError", ex.getMessage());
            LOGGER.info("Exception occurred during responding to heartbeat message: {}", String.valueOf(ex));
        }

    }

    public void updateStatus(WebSocketSession session, String uniqueId, JsonNode payloadNode) {
        // getting the chargerId from the WebSocket session
        String chargerId = (String) session.getAttributes().get("chargerId");
        String status = payloadNode.get("status").asText();
        ChargerStatusEnum statusEnum = ChargerStatusEnum.valueOf(status);
        chargerRepository.updateChargerStatus(chargerId, statusEnum);
        LOGGER.info("Updated charger status: {}", status);
        try{
            // Send success response
            String response = String.format("[3, \"%s\"]",
                    uniqueId);
            session.sendMessage(new TextMessage(response));
            LOGGER.info("Status updated for charger: {}", chargerId);
        }catch (Exception ex) {
            sendErrorResponse(session, uniqueId, "InternalError", ex.getMessage());
            LOGGER.info("Exception occurred during status update: {}", String.valueOf(ex));
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
