package com.example.realtimechat.service;

import com.example.realtimechat.entity.PrivateMessageEntity;
import com.example.realtimechat.repository.PrivateMessageRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrivateChatService {
    private final PrivateMessageRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    public PrivateChatService(PrivateMessageRepository repository, SimpMessagingTemplate messagingTemplate) {
        this.repository = repository;
        this.messagingTemplate = messagingTemplate;
    }

    public void sendPrivateMessage(String sender, String receiver, String content) {
        PrivateMessageEntity msg = PrivateMessageEntity.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .sentAt(LocalDateTime.now())
                .build();
        repository.save(msg);

        messagingTemplate.convertAndSendToUser(receiver, "/queue/private", msg);
        messagingTemplate.convertAndSendToUser(
                sender,
                "/queue/private",
                msg
        );

    }

    public List<PrivateMessageEntity> getChatHistory(String user1, String user2) {
        return repository.findBySenderAndReceiverOrReceiverAndSenderOrderBySentAtAsc(
                user1, user2,
                user1, user2
        );
    }
}
