package com.example.online.repository;

import com.example.online.domain.model.ActionBan;
import com.example.online.enumerate.BanTarget;
import com.example.online.enumerate.BanType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActionBanRepository extends JpaRepository<ActionBan, Long> {
    boolean existsByUser_IdAndActionInAndTargetId(Long userId, List<BanType> actions, Long targetId);
    Optional<ActionBan> findByUser_IdAndBanTypeAndTargetId(Long userId, BanType type, Long targetId);
    List<ActionBan> findAllByUser_IdAndBanTypeNot(Long userId, BanType banType);
    Optional<ActionBan> findByUser_IdAndBanType(Long userId, BanType banType);
    boolean existsByUser_IdAndBanType(Long userId, BanType banType);

}
