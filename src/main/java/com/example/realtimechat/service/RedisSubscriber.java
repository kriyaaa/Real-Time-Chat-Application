package com.example.realtimechat.service;

import com.example.realtimechat.entity.ChatMessageEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Handles messages published to the Redis "chat-channel" and forwards them
 * to WebSocket subscribers on /topic/public.
 *
 * The original implementation accepted raw Object — which was the JDK-serialized
 * byte[] blob — and sent it directly to the WebSocket.  That caused the client
 * to receive garbage binary data instead of JSON.
 *
 * Fix: deserialize the incoming JSON string back into a ChatMessageEntity
 * so that SimpMessagingTemplate sends a clean, typed payload.
 */
@Service
public class RedisSubscriber {

    private static final Logger log = LoggerFactory.getLogger(RedisSubscriber.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public RedisSubscriber(SimpMessagingTemplate messagingTemplate,
                           ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Called by Spring Data Redis's MessageListenerAdapter when a message
     * arrives on "chat-channel".  The argument is the raw String published
     * by RedisTemplate (JSON of ChatMessageEntity).
     */
    public void handleMessage(String message) {
        try {
            ChatMessageEntity chatMessage =
                    objectMapper.readValue(message, ChatMessageEntity.class);
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        } catch (Exception e) {
            log.error("Failed to deserialize Redis message: {}", message, e);
        }
    }
}
