package com.example.online.repository;

import com.example.online.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long userId);
    Optional<User> findBySub(String sub);
    Optional<User> findById(Long id);
}
