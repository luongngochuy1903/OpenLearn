package com.example.online.community.service.impl;

import com.example.online.community.dto.CommunityCreateRequest;
import com.example.online.enumerate.CommunityRole;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.exception.UnauthorizedException;
import com.example.online.domain.model.Community;
import com.example.online.domain.model.CommunityMember;
import com.example.online.domain.model.Tag;
import com.example.online.repository.CommunityMemberRepository;
import com.example.online.repository.CommunityRepository;
import com.example.online.community.service.CommunityService;
import com.example.online.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {
    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;

    @Transactional
    public Community createCommunity(CommunityCreateRequest communityCreateRequest){

        var user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new UnauthorizedException("You need to login first");
        }
        Set<Tag> tagSet = communityCreateRequest.getTags().stream()
                .map(tagDTO -> Tag.builder().name(tagDTO.getName()).build())
                .collect(Collectors.toSet());

        Community community = Community.builder().name(communityCreateRequest.getName())
                .description(communityCreateRequest.getDescription()).tags(tagSet)
                .createdAt(LocalDateTime.now()).build();


        CommunityMember communityMember = CommunityMember.builder().community(community)
                .user(user).role(CommunityRole.ADMIN).starContribute(0.0).build();
        communityMemberRepository.save(communityMember);
        return community;
    }

    public Community getCommunity(Long communityId){
        return communityRepository.findById(communityId).orElseThrow(() -> new ResourceNotFoundException("Community not found"));
    }

}
