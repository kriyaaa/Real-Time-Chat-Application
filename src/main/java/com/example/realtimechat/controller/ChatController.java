package com.example.realtimechat.controller;

import com.example.realtimechat.entity.ChatMessageEntity;
import com.example.realtimechat.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService service;

    public ChatController(ChatService service) {
        this.service = service;
    }

    /**
     * POST /chat/public?content=hello
     * Authenticated user sends a public message.  Published to Redis so all
     * connected instances broadcast it via /topic/public.
     */
    @PostMapping("/public")
    public void sendPublic(@RequestParam String content,
                           Principal principal) {
        service.sendPublicMessage(principal.getName(), content);
    }

    /**
     * POST /chat/private?receiver=bob&content=hello
     * Sends a private message.  If the receiver is online (Redis presence)
     * the message is delivered immediately via /user/{receiver}/queue/messages;
     * otherwise a notification is persisted for offline delivery.
     */
    @PostMapping("/private")
    public void sendPrivate(@RequestParam String receiver,
                            @RequestParam String content,
                            Principal principal) {
        service.sendPrivateMessage(principal.getName(), receiver, content);
    }

    /**
     * GET /chat/public
     * Returns the last 20 public messages (oldest first).
     */
    @GetMapping("/public")
    public List<ChatMessageEntity> getPublicMessages() {
        return service.getPublicMessages();
    }

    /**
     * GET /chat/private/{user}
     * Returns the full private conversation between the authenticated user
     * and {user}, ordered chronologically.
     */
    @GetMapping("/private/{user}")
    public List<ChatMessageEntity> getPrivateHistory(@PathVariable String user,
                                                     Principal principal) {
        return service.getPrivateHistory(principal.getName(), user);
    }

    /**
     * PATCH /chat/read/{messageId}
     * Marks a specific message as READ.  The endpoint was missing from the
     * original project even though ChatService.markAsRead() existed.
     */
    @PatchMapping("/read/{messageId}")
    public void markAsRead(@PathVariable Long messageId) {
        service.markAsRead(messageId);
    }
}
