package com.example.realtimechat.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Tracks online users using a Redis Set instead of an in-memory
 * ConcurrentHashMap.
 *
 * Reasons for the change:
 *   1. In-memory sets are lost on application restart.
 *   2. They don't work across multiple application instances (horizontal
 *      scaling), meaning a user connected to instance A would be invisible
 *      to instance B.
 *   3. Redis is already a dependency and is running; using it here adds
 *      no new infrastructure.
 *
 * The Redis key "online-users" holds a Set<String> of usernames.
 * Sessions are added on STOMP connect and removed on disconnect.
 */
@Service
public class OnlineUserService {

    private static final String ONLINE_USERS_KEY = "online-users";

    private final RedisTemplate<String, Object> redisTemplate;

    public OnlineUserService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void userConnected(String username) {
        redisTemplate.opsForSet().add(ONLINE_USERS_KEY, username);
    }

    public void userDisconnected(String username) {
        redisTemplate.opsForSet().remove(ONLINE_USERS_KEY, username);
    }

    public boolean isOnline(String username) {
        Boolean member = redisTemplate.opsForSet().isMember(ONLINE_USERS_KEY, username);
        return Boolean.TRUE.equals(member);
    }

    /**
     * Returns all currently online usernames.
     * Useful for a "who's online" REST endpoint.
     */
    public java.util.Set<Object> getOnlineUsers() {
        return redisTemplate.opsForSet().members(ONLINE_USERS_KEY);
    }
}
