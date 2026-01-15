package com.example.online.community.service;

import com.example.online.domain.model.CommunityMember;
import com.example.online.domain.model.RequestJoiningCommunity;
import com.example.online.domain.model.User;

public interface CommunityMemberService {
    boolean isAdmin(Long userId, Long communityId);
    boolean isMember(Long userId, Long communityId);
    CommunityMember findMember(Long userId, Long communityId);
    CommunityMember save(CommunityMember communityMember);
    RequestJoiningCommunity sendJoinCommunityRequest(Long communityId, User user);
}
