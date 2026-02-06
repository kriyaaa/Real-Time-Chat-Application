package com.example.realtimechat.repository;

import com.example.realtimechat.entity.PrivateMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrivateMessageRepository extends JpaRepository<PrivateMessageEntity, Long> {
    List<PrivateMessageEntity> findBySenderAndReceiverOrReceiverAndSenderOrderBySentAtAsc(
            String sender1, String receiver1,
            String sender2, String receiver2
    );
}
