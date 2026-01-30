package com.example.realtimechat.controller;

import com.example.realtimechat.entity.NotificationEntity;
import com.example.realtimechat.model.Notification;
import com.example.realtimechat.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notify")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping("/unread")
    public List<NotificationEntity> getUnread(Principal principal) {
        return service.getUnread(principal.getName());
    }

    @PostMapping("/read/{id}")
    public void markAsRead(@PathVariable Long id) {
        service.markAsRead(id);
    }
}

