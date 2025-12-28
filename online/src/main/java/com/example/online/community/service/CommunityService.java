package com.example.online.community.service;

import com.example.online.community.dto.CommunityCreateRequest;
import com.example.online.domain.model.Community;

public interface CommunityService {
    Community createCommunity(CommunityCreateRequest communityCreateRequest);
    Community getCommunity(Long communityId);
}
