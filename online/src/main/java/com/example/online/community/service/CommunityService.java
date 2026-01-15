package com.example.online.community.service;

import com.example.online.community.dto.CommunityCreateRequest;
import com.example.online.domain.model.Community;
import com.example.online.domain.model.CommunityMember;
import com.example.online.domain.model.User;

public interface CommunityService {
    Community createCommunity(CommunityCreateRequest communityCreateRequest, User user);
    void deleteCommunity(Long communityId, User user);
    Community updateCommunity(CommunityCreateRequest communityCreateRequest, Long communityId, User user);
    Community getCommunity(Long communityId);
}
