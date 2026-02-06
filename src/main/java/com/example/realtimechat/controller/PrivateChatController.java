package com.example.realtimechat.controller;

import com.example.realtimechat.entity.PrivateMessageEntity;
import com.example.realtimechat.service.PrivateChatService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/private")
public class PrivateChatController {
    private final PrivateChatService service;

    public PrivateChatController(PrivateChatService service) {
        this.service = service;
    }

    @PostMapping("/send")
    public void sendPrivate(@RequestParam String receiver, @RequestParam String content, Principal principal) {
        service.sendPrivateMessage(principal.getName(), receiver, content);

    }

    @GetMapping("/history/{user}")
    public List<PrivateMessageEntity> history(@PathVariable String user, Principal principal) {
        return service.getChatHistory(principal.getName(), user);
    }
}
