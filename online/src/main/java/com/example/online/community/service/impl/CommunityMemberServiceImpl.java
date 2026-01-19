package com.example.online.community.service.impl;

import com.example.online.community.service.CommunityService;
import com.example.online.domain.model.Community;
import com.example.online.domain.model.RequestJoiningCommunity;
import com.example.online.domain.model.User;
import com.example.online.enumerate.BanType;
import com.example.online.enumerate.CommunityRole;
import com.example.online.enumerate.CommunityStatus;
import com.example.online.enumerate.RequestStatus;
import com.example.online.exception.BadRequestException;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.domain.model.CommunityMember;
import com.example.online.exception.UnauthorizedException;
import com.example.online.repository.CommunityMemberRepository;
import com.example.online.community.service.CommunityMemberService;
import com.example.online.repository.CommunityRepository;
import com.example.online.repository.RequestJoiningCommunityRepository;
import com.example.online.utils.BanUtils;
import com.example.online.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityMemberServiceImpl implements CommunityMemberService {
    private final CommunityMemberRepository communityMemberRepository;
    private final CommunityRepository communityRepository;
    private final RequestJoiningCommunityRepository requestJoiningCommunityRepository;
    private final BanUtils banUtils;
    private static final Logger LOG = LoggerFactory.getLogger(CommunityMemberServiceImpl.class);

    /*
        Function: Send Joining member request
        Business Context Role: Community Member
     */
    public RequestJoiningCommunity sendJoinCommunityRequest(Long communityId, User user){
        if (user == null) {
            throw new UnauthorizedException("You need to login first");
        }
        banUtils.checkBan(user.getId(), BanType.COMMUNITY, communityId);
        RequestJoiningCommunity requestJoiningCommunity = requestJoiningCommunityRepository
                .findByUser_IdAndCommunity_IdAndStatus(user.getId(), communityId, CommunityStatus.JOINING_PENDING).orElse(null);

        if (requestJoiningCommunity != null) {
            CommunityStatus status = requestJoiningCommunity.getStatus();

            if (status == CommunityStatus.JOINING_PENDING) {
                throw new BadRequestException("Request is already pending");
            }

        }

        RequestJoiningCommunity request = createRequestJoiningCommunity(communityId, CommunityStatus.JOINING_PENDING, user);
        LOG.info("User {} send joining request to community {}", user.getEmail(), communityId);
        return request;
    }

    public boolean isAdmin(Long userId, Long communityId){
        if (userId == null){
            throw new BadRequestException("userId could not be null");
        }
        if (communityId == null){
            throw new BadRequestException("communityId could not be null");
        }
        return communityMemberRepository.existsByUser_IdAndCommunity_IdAndRoleIn(userId, communityId, List.of(CommunityRole.ADMIN));
    }

    public boolean isMember(Long userId, Long communityId){
        if (userId == null){
            throw new BadRequestException("userId could not be null");
        }
        if (communityId == null){
            throw new BadRequestException("communityId could not be null");
        }
        return communityMemberRepository.existsByUser_IdAndCommunity_IdAndRoleIn(userId, communityId, List.of(CommunityRole.MEMBER, CommunityRole.ADMIN));
    }

    public CommunityMember findMember(Long userId, Long communityId){
        if (userId == null){
            throw new BadRequestException("userId could not be null");
        }
        if (communityId == null){
            throw new BadRequestException("communityId could not be null");
        }
        return communityMemberRepository.findByUser_IdAndCommunity_Id(userId, communityId)
                .orElseThrow(() -> new ResourceNotFoundException("User in community not found!"));
    }

    public CommunityMember save(CommunityMember communityMember){
        return communityMemberRepository.save(communityMember);
    }

    public RequestJoiningCommunity createRequestJoiningCommunity(Long communityId, CommunityStatus communityStatus, User user){
        if (communityId == null){
            throw new BadRequestException("communityId could not be null");
        }
        Community community = communityRepository.findById(communityId).orElseThrow(() -> new ResourceNotFoundException("Community not found"));
        return requestJoiningCommunityRepository.save(RequestJoiningCommunity.builder().user(user).community(community).status(communityStatus)
                .createdAt(LocalDateTime.now()).build()
        );
    }
}
