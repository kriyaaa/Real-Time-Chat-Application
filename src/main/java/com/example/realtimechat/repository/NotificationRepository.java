package com.example.realtimechat.repository;

import com.example.realtimechat.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByUsernameAndReadFalse(String username);
}
