package com.example.online.repository;

import com.example.online.domain.model.NotificationUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationUserRepository extends JpaRepository<NotificationUser, Long> {
}
