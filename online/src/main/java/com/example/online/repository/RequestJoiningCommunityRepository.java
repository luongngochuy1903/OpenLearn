package com.example.online.repository;

import com.example.online.domain.model.RequestJoiningCommunity;
import com.example.online.enumerate.CommunityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequestJoiningCommunityRepository extends JpaRepository<RequestJoiningCommunity, Long> {
    Optional<RequestJoiningCommunity> findByUser_IdAndCommunity_Id(Long userId, Long communityId);
    Page<RequestJoiningCommunity> findByCommunityId(Long communityId, Pageable pageable);
    boolean existsByUser_IdAndStatus(Long userId, CommunityStatus status);
}
