package com.example.realtimechat.service;

import com.example.realtimechat.entity.ChatMessageEntity;
import com.example.realtimechat.entity.MessageStatus;
import com.example.realtimechat.entity.MessageType;
import com.example.realtimechat.repository.ChatMessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository repository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;
    private final OnlineUserService onlineUserService;
    private final RedisTemplate<String, Object> redisTemplate;

    public ChatService(ChatMessageRepository repository,
                       SimpMessagingTemplate messagingTemplate,
                       NotificationService notificationService,
                       OnlineUserService onlineUserService,
                       RedisTemplate<String, Object> redisTemplate) {
        this.repository = repository;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
        this.onlineUserService = onlineUserService;
        this.redisTemplate = redisTemplate;
    }



    @Transactional
    public void sendPublicMessage(String sender, String content) {

        ChatMessageEntity message = ChatMessageEntity.builder()
                .sender(sender)
                .content(content)
                .type(MessageType.PUBLIC)
                .status(MessageStatus.SENT)
                .sentAt(LocalDateTime.now())
                .build();

        repository.save(message);

        redisTemplate.convertAndSend("chat-channel", message);
    }

    @Transactional
    public void sendPrivateMessage(String sender, String receiver, String content) {

        ChatMessageEntity message = ChatMessageEntity.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .type(MessageType.PRIVATE)
                .status(MessageStatus.SENT)
                .sentAt(LocalDateTime.now())
                .build();

        repository.save(message);

        if (onlineUserService.isOnline(receiver)) {

            message.setStatus(MessageStatus.DELIVERED);
            repository.save(message);

            messagingTemplate.convertAndSendToUser(
                    receiver,
                    "/queue/messages",
                    message
            );

        } else {

            notificationService.sendNotification(
                    receiver,
                    "Private message from " + sender
            );
        }
    }

    @Transactional
    public void markAsRead(Long messageId) {

        ChatMessageEntity message = repository.findById(messageId)
                .orElseThrow();

        message.setStatus(MessageStatus.READ);
        repository.save(message);
    }

    public List<ChatMessageEntity> getPublicMessages() {
        return repository.findTop20ByTypeOrderBySentAtAsc(MessageType.PUBLIC);
    }

    public List<ChatMessageEntity> getPrivateHistory(String user1, String user2) {
        return repository.findPrivateChat(user1, user2);
    }
}
