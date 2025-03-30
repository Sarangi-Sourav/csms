package com.zynetic.ev_charger_management.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final OcppWebSocketHandler ocppWebSocketHandler;
    private final ChargePointHandshakeInterceptor chargePointHandshakeInterceptor;

    public WebSocketConfig(OcppWebSocketHandler ocppWebSocketHandler, ChargePointHandshakeInterceptor chargePointHandshakeInterceptor) {
        this.ocppWebSocketHandler = ocppWebSocketHandler;
        this.chargePointHandshakeInterceptor = chargePointHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(ocppWebSocketHandler, "/ocpp1.6j/{chargerId}")
                .addInterceptors(chargePointHandshakeInterceptor)
                .setAllowedOrigins("*");
    }
    public TaskExecutor webSocketTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(25);
        executor.setMaxPoolSize(101);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("ws");
        executor.initialize();
        return executor;
    }
}
