package com.example.realtimechat.repository;

import com.example.realtimechat.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    // public chat
    List<ChatMessageEntity> findTop20ByReceiverIsNullOrderBySentAtAsc();
    //private chat
    List<ChatMessageEntity> findBySenderAndReceiverOrSenderAndReceiverOrderBySentAtAsc(
            String sender1, String receiver1,
            String sender2, String receiver2
    );
}
