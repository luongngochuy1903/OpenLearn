package com.example.online.repository;

import com.example.online.domain.model.CommunityMember;
import com.example.online.enumerate.CommunityRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {
    Optional<CommunityMember> findByUser_IdAndCommunity_Id(Long userId, Long communityId);
    boolean existsByUser_IdAndCommunity_IdAndRoleIn(Long userId, Long communityId,
                                                    Collection<CommunityRole> roles
    );
}
