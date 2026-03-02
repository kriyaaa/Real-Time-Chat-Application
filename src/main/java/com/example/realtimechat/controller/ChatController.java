package com.example.realtimechat.controller;

import com.example.realtimechat.entity.ChatMessageEntity;
import com.example.realtimechat.model.PrivateMessage;
import com.example.realtimechat.repository.ChatMessageRepository;
import com.example.realtimechat.service.ChatService;
import com.example.realtimechat.service.NotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService service;

    public ChatController(ChatService service) {
        this.service = service;
    }

    @PostMapping("/public")
    public void sendPublic(@RequestParam String content,
                           Principal principal) {
        service.sendPublicMessage(principal.getName(), content);
    }

    @PostMapping("/private")
    public void sendPrivate(@RequestParam String receiver,
                            @RequestParam String content,
                            Principal principal) {
        service.sendPrivateMessage(principal.getName(), receiver, content);
    }

    @GetMapping("/public")
    public List<ChatMessageEntity> getPublicMessages() {
        return service.getPublicMessages();
    }

    @GetMapping("/private/{user}")
    public List<ChatMessageEntity> getPrivateHistory(@PathVariable String user,
                                                     Principal principal) {
        return service.getPrivateHistory(principal.getName(), user);
    }
}

