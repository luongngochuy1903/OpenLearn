package com.example.online.community.service.impl;

import com.example.online.annotation.CheckCommunityAdmin;
import com.example.online.community.dto.MemberRequestResponse;
import com.example.online.community.service.CommunityAdminService;
import com.example.online.community.service.CommunityMemberService;
import com.example.online.community.service.CommunityService;
import com.example.online.domain.model.Community;
import com.example.online.domain.model.CommunityMember;
import com.example.online.domain.model.RequestJoiningCommunity;
import com.example.online.domain.model.User;
import com.example.online.enumerate.CommunityRole;
import com.example.online.enumerate.CommunityStatus;
import com.example.online.exception.BadRequestException;
import com.example.online.exception.UnauthorizedException;
import com.example.online.repository.RequestJoiningCommunityRepository;
import com.example.online.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityAdminServiceImpl implements CommunityAdminService {
    private final UserService userService;
    private final CommunityService communityService;
    private final CommunityMemberService communityMemberService;
    private final RequestJoiningCommunityRepository requestJoiningCommunityRepository;
    private static final Logger LOG = LoggerFactory.getLogger(CommunityAdminServiceImpl.class);
    /*
        Function: Add member to community
        Business Context Role: Community Admin
     */
    @CheckCommunityAdmin(communityIdParam = "communityId", userParam = "user")
    public CommunityMember approveCommunityMember(Long communityId, Long memberId, User user){
        if (user == null) {
            throw new UnauthorizedException("You need to login first");
        }

        if(memberId == null){
            throw new BadRequestException("memberId could not be null");
        }

        RequestJoiningCommunity requestJoiningCommunity = requestJoiningCommunityRepository
                .findByUser_IdAndCommunity_Id(memberId, communityId).orElse(null);
        if (requestJoiningCommunity == null || requestJoiningCommunity.getStatus().equals(CommunityStatus.BLOCK)){
            LOG.info("User {} cannot be added to community {} by {}", memberId, communityId, user.getEmail());
            return null;
        }

        var member = userService.findUserById(memberId);
        Community community = communityService.getCommunity(communityId);
        CommunityMember communityMember = CommunityMember.builder().community(community)
                .user(member).role(CommunityRole.MEMBER).starContribute(0.0).build();

        communityMemberService.save(communityMember);
        requestJoiningCommunityRepository.delete(requestJoiningCommunity);
        LOG.info("User {} is added to community {} - {} by {}", member.getEmail(), community.getId(), community.getName(), user.getEmail());
        return communityMember;
    }

    /*
        Function: Decline member request to community
        Business Context Role: Community Admin
     */
    @CheckCommunityAdmin(communityIdParam = "communityId", userParam = "user")
    public void declineCommunityMember(Long communityId, Long memberId, User user){
        if (user == null) {
            throw new UnauthorizedException("You need to login first");
        }

        if(memberId == null){
            throw new BadRequestException("memberId could not be null");
        }
        RequestJoiningCommunity requestJoiningCommunity = requestJoiningCommunityRepository
                .findByUser_IdAndCommunity_Id(memberId, communityId).orElseThrow(() -> new BadRequestException("There are something wrong! Try again later"));

        requestJoiningCommunityRepository.delete(requestJoiningCommunity);
    }

    /*
        Function: Block member from joining community
        Business Context Role: Community Admin
     */
    @CheckCommunityAdmin(communityIdParam = "communityId", userParam = "user")
    public void blockMember(Long communityId, Long memberId, User user){
        if (user == null) {
            throw new UnauthorizedException("You need to login first");
        }

        if(memberId == null){
            throw new BadRequestException("memberId could not be null");
        }
        RequestJoiningCommunity requestJoiningCommunity = requestJoiningCommunityRepository
                .findByUser_IdAndCommunity_Id(memberId, communityId).orElseThrow(() -> new BadRequestException("There are something wrong! Try again later"));

        requestJoiningCommunity.setStatus(CommunityStatus.BLOCK);
        requestJoiningCommunityRepository.save(requestJoiningCommunity);
    }

    @CheckCommunityAdmin(communityIdParam = "communityId", userParam = "user")
    public Page<MemberRequestResponse> getRequestJoiningTable(Long communityId, Pageable pageable, User user){
        if (user == null) {
            throw new UnauthorizedException("You need to login first");
        }

        LOG.info("User {} loaded community {} 's accept request table - Page", user.getEmail(), communityId);
        Page<RequestJoiningCommunity> reqList = requestJoiningCommunityRepository.findByCommunityId(communityId, pageable);
        return reqList.map(req ->
                        MemberRequestResponse.builder().memberId(req.getUser().getId())
                                .name(req.getUser().getFirstName() + " " + req.getUser().getLastName())
                                .status(String.valueOf(req.getStatus()))
                                .createdAt(req.getCreatedAt())
                                .build()
                );
    }

    public List<RequestJoiningCommunity> getAllRequestJoiningTable(Long communityId, User user){
        if (user == null) {
            throw new UnauthorizedException("You need to login first");
        }

        LOG.info("User {} loaded community {} 's accept request table - List", user.getEmail(), communityId);
        return requestJoiningCommunityRepository.findByCommunityId(communityId, Pageable.unpaged()).getContent();
    }
}
