package com.zynetic.ev_charger_management.service;

import com.zynetic.ev_charger_management.configuration.OcppWebSocketHandler;
import com.zynetic.ev_charger_management.repository.ChargerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class HeartbeatMonitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatMonitor.class);
    private final Map<String, Timestamp> lastHeartbeat = new ConcurrentHashMap<>();

    private final ChargerRepository chargerRepository;
    private final OcppWebSocketHandler ocppWebSocketHandler;

    public HeartbeatMonitor(ChargerRepository chargerRepository, OcppWebSocketHandler ocppWebSocketHandler) {
        this.chargerRepository = chargerRepository;
        this.ocppWebSocketHandler = ocppWebSocketHandler;
    }

    @Scheduled(fixedRate = 60000) // Runs every minute
    @Transactional
    public void markUnavailableChargers() {
        // marking the chargers unavailable that hasn't sent any heartbeat for 5+ minutes ( keeping it 6 minutes 1 minute as buffer )
        Instant fiveMinutesAgo = Instant.now().minus(6, ChronoUnit.MINUTES);

        lastHeartbeat.forEach((chargerId, lastPing) -> {
            LOGGER.warn("Charger {} is inactive, closing the connection.", chargerId);
            WebSocketSession session = ocppWebSocketHandler.getSession(chargerId);
            if (session != null) {
                try{
                    session.close();
                } catch (IOException e) {
                    LOGGER.error("Failed to close WebSocket session for {}", chargerId);
                }
                lastHeartbeat.remove(chargerId);
            }
        });
        int updatedRows = chargerRepository.markChargersUnavailable(Timestamp.from(fiveMinutesAgo));
        if (updatedRows > 0) {
            LOGGER.warn("{} chargers marked as Unavailable due to no heartbeat.", updatedRows);
        }
    }
    public void updateHeartbeat(String changerId){
        lastHeartbeat.put(changerId, Timestamp.from(Instant.now()));
    }
}
