package com.example.realtimechat.controller;

import com.example.realtimechat.entity.ChatMessageEntity;
import com.example.realtimechat.model.PrivateMessage;
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
                .receiver(null)
                .content(content)
                .sentAt(LocalDateTime.now())
                .build();

        repository.save(message);

        messagingTemplate.convertAndSend("/topic/messages", message);

        // Notify others
        notificationService.sendNotification(principal.getName(), "New message received from "+principal.getName());

    }

    @PostMapping("/chat/private")
    @ResponseBody
    public void sendPrivateMessage(@RequestBody PrivateMessage msg,Principal principal){
        String sender = principal.getName();
        ChatMessageEntity message = ChatMessageEntity.builder()
                .sender(sender)
                .receiver(msg.getReceiver())
                .content(msg.getContent())
                .sentAt(LocalDateTime.now())
                .build();
        repository.save(message);
        // send only to receiver
        messagingTemplate.convertAndSendToUser(msg.getReceiver(), "/queue/messages", message);
        // notify receiver
        notificationService.sendNotification(msg.getReceiver(), "Private message from "+sender);

    }

    @GetMapping("/chat/messages")
    @ResponseBody
    public List<ChatMessageEntity> getAllMessages() {
        return repository.findTop20ByReceiverIsNullOrderBySentAtAsc();
    }

    @GetMapping("/chat/private/history/{user}")
    @ResponseBody
    public List<ChatMessageEntity> getPrivateChat(@PathVariable String user, Principal principal) {
        return repository.findBySenderAndReceiverOrSenderAndReceiverOrderBySentAtAsc(principal.getName(), user, user, principal.getName());
    }
}

