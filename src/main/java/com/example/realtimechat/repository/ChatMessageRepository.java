package com.example.realtimechat.repository;

import com.example.realtimechat.entity.ChatMessageEntity;
import com.example.realtimechat.entity.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    // public chat
    List<ChatMessageEntity> findTop20ByTypeOrderBySentAtAsc(MessageType type);
    @Query("""
        SELECT m FROM ChatMessageEntity m
        WHERE m.type = 'PRIVATE'
        AND (
            (m.sender = :user1 AND m.receiver = :user2)
            OR
            (m.sender = :user2 AND m.receiver = :user1)
        )
        ORDER BY m.sentAt ASC
    """)
    List<ChatMessageEntity> findPrivateChat(String user1, String user2);
}
