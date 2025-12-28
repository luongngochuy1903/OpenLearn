package com.example.online.communitymember.service.impl;

import com.example.online.enumerate.CommunityRole;
import com.example.online.exception.UnauthorizedException;
import com.example.online.domain.model.Community;
import com.example.online.domain.model.CommunityMember;
import com.example.online.repository.CommunityMemberRepository;
import com.example.online.communitymember.service.CommunityMemberService;
import com.example.online.community.service.CommunityService;
import com.example.online.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommunityMemberServiceImpl implements CommunityMemberService {
    private final CommunityMemberRepository communityMemberRepository;
    private final CommunityService communityService;

    public CommunityMember addCommunityMember(Long communityId){
        var user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new UnauthorizedException("You need to login first");
        }
        Community community = communityService.getCommunity(communityId);
        CommunityMember communityMember = CommunityMember.builder().community(community)
                .user(user).role(CommunityRole.MEMBER).starContribute(0.0).build();

        communityMemberRepository.save(communityMember);
        return communityMember;
    }
}
