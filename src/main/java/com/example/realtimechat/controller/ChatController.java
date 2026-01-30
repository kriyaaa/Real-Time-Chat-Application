package com.example.realtimechat.controller;

import com.example.realtimechat.entity.ChatMessageEntity;
import com.example.realtimechat.repository.ChatMessageRepository;
import com.example.realtimechat.service.NotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository repository;
    private final NotificationService notificationService;

    public ChatController(SimpMessagingTemplate messagingTemplate,
                          ChatMessageRepository repository,
                          NotificationService notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.repository = repository;
        this.notificationService = notificationService;
    }

    @PostMapping("/chat/send")
    @ResponseBody
    public void sendMessage(@RequestParam String content,
                            Principal principal) {

        ChatMessageEntity message = ChatMessageEntity.builder()
                .sender(principal.getName())
                .content(content)
                .sentAt(LocalDateTime.now())
                .build();

        repository.save(message);

        messagingTemplate.convertAndSend("/topic/messages", message);

        // Notify others
        notificationService.sendNotification(principal.getName(), "New message received");

    }
    @GetMapping("/chat/messages")
    @ResponseBody
    public List<ChatMessageEntity> getAllMessages() {
        return repository.findAllByOrderBySentAtAsc();
    }
}

