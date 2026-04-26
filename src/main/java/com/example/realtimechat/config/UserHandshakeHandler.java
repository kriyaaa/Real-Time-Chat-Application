package com.example.realtimechat.config;

import com.example.realtimechat.security.JwtService;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * Extracts the authenticated user from the JWT token supplied during the
 * WebSocket STOMP handshake.  The client must pass the token either as:
 *   - An HTTP header:  Authorization: Bearer <token>
 *   - A query param:   /ws?token=<token>
 *
 * Previously this was hardcoded to "riya", which broke JWT-based identity for
 * every WebSocket connection.  This fix reads the real token and resolves the
 * username so that convertAndSendToUser() routes correctly.
 */
@Component
public class UserHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtService jwtService;

    public UserHandshakeHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        String token = extractToken(request);

        if (token == null) {
            // Return null so Spring rejects the connection; SecurityConfig will
            // also block unauthenticated WS upgrades once that is tightened.
            return null;
        }

        try {
            String username = jwtService.extractUsername(token);
            if (username == null) return null;
            // Store username in attributes for downstream use (e.g. event listeners)
            attributes.put("username", username);
            return () -> username;
        } catch (Exception e) {
            return null;
        }
    }

    private String extractToken(ServerHttpRequest request) {
        // 1. Authorization header
        List<String> authHeaders = request.getHeaders().get("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String header = authHeaders.get(0);
            if (header.startsWith("Bearer ")) {
                return header.substring(7);
            }
        }

        // 2. Query parameter ?token=...
        String query = request.getURI().getQuery();
        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    return param.substring(6);
                }
            }
        }

        return null;
    }
}
