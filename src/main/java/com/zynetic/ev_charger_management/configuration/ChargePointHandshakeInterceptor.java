package com.zynetic.ev_charger_management.configuration;


import com.zynetic.ev_charger_management.repository.ChargerCredRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ChargePointHandshakeInterceptor implements HandshakeInterceptor {
    // we can add all the constant value in a final properties class or properties file itself to fetch from there and for easy changes
    private static final String SEC_WEBSOCKET_PROTOCOL_HEADER = "Sec-WebSocket-Protocol";
    private static final String OCPP_SUB_PROTOCOL = "ocpp1.6";
    private static final String AUTH_HEADER = "Authorization";

    private static final Logger LOGGER = LoggerFactory.getLogger(ChargePointHandshakeInterceptor.class);

    private final ChargerCredRepository chargerCredRepository;

    public ChargePointHandshakeInterceptor(ChargerCredRepository chargerCredRepository) {
        this.chargerCredRepository = chargerCredRepository;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        return isAuthValid(request, attributes) && doesSubProtocolExitsAndValid(request, response);
    }

    private boolean doesSubProtocolExitsAndValid(ServerHttpRequest request, ServerHttpResponse response){
        List<String> subProtocols = request.getHeaders().get(SEC_WEBSOCKET_PROTOCOL_HEADER);
        boolean isCorrectSubProtocol = subProtocols != null && subProtocols.stream().anyMatch(n -> n.contains(OCPP_SUB_PROTOCOL));
        if (isCorrectSubProtocol && !subProtocols.isEmpty()){
            response.getHeaders().add(SEC_WEBSOCKET_PROTOCOL_HEADER, OCPP_SUB_PROTOCOL);
            LOGGER.info("The sub protocol is set as: {}", OCPP_SUB_PROTOCOL );
            return true;
        } else {
            LOGGER.info("We are currently not supporting the sub-protocols: {}", subProtocols);
            return false;
        }
    }

    private boolean isAuthValid(ServerHttpRequest request, Map<String, Object> attributes){
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return false;
        }
        HttpServletRequest httpRequest = servletRequest.getServletRequest();
        String path = httpRequest.getRequestURI(); // Extract URL path
        String[] pathSegments = path.split("/");

        if (pathSegments.length < 3) {
            return false; // Invalid URL format as chargerId is a must
        }
        String chargerId = pathSegments[2];

        // Extract pass-key from WebSocket Headers
        String passKey = httpRequest.getHeader(AUTH_HEADER);
        if (passKey == null) {
            return false; // Reject if Authorization header is missing
        }
        Optional<String> charger = chargerCredRepository.findPassKeyByChargerId(chargerId);
        if (charger.isEmpty()) {
            return false; // Reject Authorization if no ID is found
        }
        String storedHashedKey = charger.get();

        // Validate pass-key using BCrypt
        if (!BCrypt.checkpw(passKey, storedHashedKey)) {
            LOGGER.info("The user entered passKey is not valid");
            return false; // Reject if password does not match
        }

        // Store charger_id in attributes for later use
        attributes.put("chargerId", chargerId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
