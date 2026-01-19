package com.example.online.repository;

import com.example.online.domain.model.ActionBan;
import com.example.online.enumerate.BanTarget;
import com.example.online.enumerate.BanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ActionBanRepository extends JpaRepository<ActionBan, Long> {
    @Query("""
    SELECT COUNT(b) > 0
        FROM ActionBan b
        WHERE b.user.id = :userId
          AND (
               (b.action = 'EVERYTHING' AND b.targetId IS NULL)
            OR (b.action = :action AND b.targetId = :targetId)
          )
    """)
    boolean isBanned(Long userId, BanType action, Long targetId);
    Optional<ActionBan> findByUser_IdAndActionAndTargetId(Long userId, BanType type, Long targetId);
    List<ActionBan> findAllByUser_IdAndActionNot(Long userId, BanType banType);
    Optional<ActionBan> findByUser_IdAndAction(Long userId, BanType banType);
    boolean existsByUser_IdAndAction(Long userId, BanType banType);

}
