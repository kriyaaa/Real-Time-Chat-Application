package com.example.realtimechat.controller;

import com.example.realtimechat.service.OnlineUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * Exposes online presence information via REST.
 *
 * GET /api/presence/online  →  set of currently online usernames
 *
 * This was not present in the original project.  It complements the
 * WebSocket event listener (connect/disconnect) and the Redis-backed
 * OnlineUserService, giving REST clients a way to bootstrap their UI
 * with the current presence state before WebSocket updates arrive.
 */
@RestController
@RequestMapping("/api/presence")
public class UserPresenceController {

    private final OnlineUserService onlineUserService;

    public UserPresenceController(OnlineUserService onlineUserService) {
        this.onlineUserService = onlineUserService;
    }

    @GetMapping("/online")
    public Set<Object> getOnlineUsers() {
        return onlineUserService.getOnlineUsers();
    }
}
