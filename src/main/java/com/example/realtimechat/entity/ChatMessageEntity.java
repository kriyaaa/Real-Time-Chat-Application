package com.example.realtimechat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages",
        indexes = {
                @Index(name = "idx_sender", columnList = "sender"),
                @Index(name = "idx_receiver", columnList = "receiver"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_sentAt", columnList = "sentAt")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = true)
    private String receiver; // null = public message

    @Column(nullable = false, length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type; // PUBLIC or PRIVATE

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status; // SENT, DELIVERED, READ

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @PrePersist
    public void prePersist() {
        this.sentAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = MessageStatus.SENT;
        }
    }
}

