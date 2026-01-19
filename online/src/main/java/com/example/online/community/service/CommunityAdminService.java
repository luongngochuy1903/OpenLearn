package com.example.online.community.service;

import com.example.online.community.dto.MemberRequestResponse;
import com.example.online.domain.model.CommunityMember;
import com.example.online.domain.model.RequestJoiningCommunity;
import com.example.online.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommunityAdminService {
    CommunityMember approveCommunityMember(Long communityId, Long memberId, User user);
    void declineCommunityMember(Long communityId, Long memberId, User user);
    Page<MemberRequestResponse> getRequestJoiningTable(Long communityId, Pageable pageable, User user);
    List<RequestJoiningCommunity> getAllRequestJoiningTable(Long communityId, User user);
    void blockMember(Long communityId, Long memberId, User user);
    void removeBlockMember(Long communityId, Long memberId, User user);
}
