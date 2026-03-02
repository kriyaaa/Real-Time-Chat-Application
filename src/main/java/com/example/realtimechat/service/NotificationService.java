package com.example.realtimechat.service;

import com.example.realtimechat.entity.NotificationEntity;
import com.example.realtimechat.repository.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository repository,
                               SimpMessagingTemplate messagingTemplate) {
        this.repository = repository;
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(String username, String message) {

        NotificationEntity entity = NotificationEntity.builder()
                .username(username)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(entity);

        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/notifications",
                entity
        );
    }

    public List<NotificationEntity> getUnread(String username) {
        return repository.findByUsernameAndReadFalse(username);
    }

    public void markAsRead(Long id) {
        repository.findById(id).ifPresent(n -> {
            n.setRead(true);
            repository.save(n);
        });
    }
}
