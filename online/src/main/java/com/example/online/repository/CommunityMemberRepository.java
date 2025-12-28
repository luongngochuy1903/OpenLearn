package com.example.online.repository;

import com.example.online.domain.model.CommunityMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {
}
