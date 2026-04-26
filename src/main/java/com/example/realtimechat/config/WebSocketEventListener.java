package com.example.realtimechat.config;

import com.example.realtimechat.service.OnlineUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final OnlineUserService onlineUserService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = resolveUsername(accessor);
        if (username != null) {
            onlineUserService.userConnected(username);
            broadcastPresence(username, "ONLINE");
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = resolveUsername(accessor);
        if (username != null) {
            onlineUserService.userDisconnected(username);
            broadcastPresence(username, "OFFLINE");
        }
    }

    private String resolveUsername(StompHeaderAccessor accessor) {
        Map<String, Object> attrs = accessor.getSessionAttributes();
        if (attrs != null && attrs.containsKey("username")) {
            return (String) attrs.get("username");
        }
        Principal user = accessor.getUser();
        return user != null ? user.getName() : null;
    }

    private void broadcastPresence(String username, String status) {
        messagingTemplate.convertAndSend("/topic/presence",
                new PresencePayload(username, status));
    }

    public record PresencePayload(String username, String status) {}
}