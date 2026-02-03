package com.example.online.repository;

import com.example.online.domain.model.OnetimeToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OnetimeTokenRepository extends JpaRepository<OnetimeToken, Long> {
    Optional<OnetimeToken> findByCodeHash(String codeHash);
}
