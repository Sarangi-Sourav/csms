package com.zynetic.ev_charger_management.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zynetic.ev_charger_management.service.BootNotificationService;
import com.zynetic.ev_charger_management.service.ChargerService;
import com.zynetic.ev_charger_management.service.ChargerTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OcppWebSocketHandler extends TextWebSocketHandler {

    private static final int CALL = 2;
    private static final int CALLRESULT = 3;
    private static final int CALLERROR = 4;

    private static final Logger LOGGER = LoggerFactory.getLogger(OcppWebSocketHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private final ChargerTransactionService  chargerTransactionService;
    private final BootNotificationService bootNotificationService;
    private final ChargerService chargerService;

    private final Set<String> uniqueIds = new HashSet<>();
    private final Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    public OcppWebSocketHandler(ChargerTransactionService chargerTransactionService, BootNotificationService bootNotificationService, ChargerService chargerService) {
        this.chargerTransactionService = chargerTransactionService;
        this.bootNotificationService = bootNotificationService;
        this.chargerService = chargerService;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String chargerId = extractChargerId(session);
        activeSessions.put(chargerId, session);
        LOGGER.info("WebSocket connection established with chargerId{}:", chargerId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // processing the incoming messages in async
        CompletableFuture.runAsync(() -> processMessage(session, message));
    }

    private void processMessage(WebSocketSession session, TextMessage message) {
        try {
            JsonNode rootNode = objectMapper.readTree(message.getPayload());

            int messageTypeId = rootNode.get(0).asInt();
            String uniqueId = rootNode.get(1).asText();
            String action = rootNode.get(2).asText();
            JsonNode payloadNode = rootNode.get(3);

            // Validate the messageTypeId for CALL request from the client ( it can be different so this is temporary for now ) ** need to update
            if(CALL != messageTypeId) throw new Exception("The messageTypeId is not valid");
            // Validate the uniqueId is unique per chargerId in a single websocket connection
            boolean isUnique = uniqueIds.add(uniqueId);
            if(!isUnique) throw new Exception("The uniqueId has to be unique for each request");
            // Process based on action
            switch (action) {
                case "RemoteStartTransactionRequest":
                    LOGGER.info("Entering the chargerTransactionService...");
                    chargerTransactionService.processRemoteStartTransaction(session, uniqueId, payloadNode);
                    break;
                case "BootNotification":
                    LOGGER.info("Entering the bootNotificationService...");
                    bootNotificationService.processBootNotification(session, uniqueId, payloadNode);
                    break;
                case "HeartbeatRequest":
                    LOGGER.info("Entering the chargerService for heartbeat...");
                    chargerService.handleHeartbeat(session, uniqueId, payloadNode);
                    break;
                case "StatusNotificationRequest":
                    LOGGER.info("Entering the chargerService for statusNotification...");
                    chargerService.updateStatus(session, uniqueId, payloadNode);
                    break;
                default:
                    String errorResponse = String.format("[%s, \"%s\", \"NotSupported\", \"Action %s is not supported\", {}]",
                            CALLERROR,uniqueId, action);
                    session.sendMessage(new TextMessage(errorResponse));
            }
        } catch (Exception ex) {
            // If any parsing or validation fails, send back a CallError
            String errorResponse = String.format("[%s, \"unknown\", \"FormationViolation\", \"Invalid message format: %s\", {}]",
                    CALLERROR,ex.getMessage());
            try {
                session.sendMessage(new TextMessage(errorResponse));
            } catch (Exception e) {
                LOGGER.error("Error sending response: {}", e.getMessage());
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String chargerId = extractChargerId(session);
        activeSessions.remove(chargerId);
        LOGGER.info("WebSocket connection disconnected with chargerId{}:", chargerId);
    }

    private String extractChargerId(WebSocketSession session) {
        return Objects.requireNonNull(session.getUri()).getPath().split("/")[2];
    }

    public WebSocketSession getSession(String chargerId){
        return activeSessions.get(chargerId);
    }
}
