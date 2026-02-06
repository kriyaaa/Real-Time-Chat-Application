package com.example.realtimechat.service;

import com.example.realtimechat.entity.ChatMessageEntity;
import com.example.realtimechat.repository.ChatMessageRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository repository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    public ChatService(ChatMessageRepository repository,
                       SimpMessagingTemplate messagingTemplate,
                       NotificationService notificationService) {
        this.repository = repository;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    public void sendMessage(String sender, String content) {
        ChatMessageEntity entity = ChatMessageEntity.builder()
                .sender(sender)
                .content(content)
                .sentAt(LocalDateTime.now())
                .build();

        repository.save(entity);
        messagingTemplate.convertAndSend("/topic/messages", entity);
        notificationService.sendNotification("riya", "new message from" + sender);
    }

    public List<ChatMessageEntity> getRecentMessages() {

        return repository.findTop20ByReceiverIsNullOrderBySentAtAsc();
    }
}
