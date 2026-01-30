package com.example.realtimechat.repository;

import com.example.realtimechat.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findAllByOrderBySentAtAsc();
}
