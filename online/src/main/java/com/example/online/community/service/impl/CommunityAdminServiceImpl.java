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
import com.example.online.enumerate.BanType;
import com.example.online.enumerate.CommunityRole;
import com.example.online.enumerate.CommunityStatus;
import com.example.online.exception.BadRequestException;
import com.example.online.exception.UnauthorizedException;
import com.example.online.repository.RequestJoiningCommunityRepository;
import com.example.online.user.service.UserService;
import com.example.online.utils.BanUtils;
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
    private final BanUtils banUtils;
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

        if (banUtils.checkUserBan(memberId, BanType.COMMUNITY, communityId)) {
            throw new BadRequestException(String.format("User %s has been banned from this action", user.getFirstName() + " " + user.getLastName()));
        }

        RequestJoiningCommunity requestJoiningCommunity = requestJoiningCommunityRepository
                .findByUser_IdAndCommunity_IdAndStatus(memberId, communityId, CommunityStatus.JOINING_PENDING).orElseThrow(() -> new BadRequestException("Request not found!"));

        var member = userService.findUserById(memberId);
        CommunityMember communityMember = CommunityMember.builder().community(requestJoiningCommunity.getCommunity())
                .user(member).role(CommunityRole.MEMBER).starContribute(0.0).build();

        communityMemberService.save(communityMember);
        requestJoiningCommunityRepository.delete(requestJoiningCommunity);
        LOG.info("User {} is added to community {} - {} by {}", member.getEmail(), requestJoiningCommunity.getCommunity().getId(), requestJoiningCommunity.getCommunity().getName(), user.getEmail());
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
                .findByUser_IdAndCommunity_IdAndStatus(memberId, communityId, CommunityStatus.JOINING_PENDING).orElseThrow(() -> new BadRequestException("There are something wrong! Try again later"));

        requestJoiningCommunityRepository.delete(requestJoiningCommunity);
    }

    /*
        Function: Block member from joining community
        Business Context Role: Community Admin
     */
    @CheckCommunityAdmin(communityIdParam = "communityId", userParam = "user")
    public void blockMember(Long communityId, Long memberId, User user){
        if (memberId == null){
            throw new BadRequestException("courseId could not be null");
        }

        banUtils.addBanRecord(memberId, BanType.COMMUNITY, communityId);

        LOG.info("{} banned user with id {} from his/her community {}", user.getFirstName() + " " + user.getLastName(),
                memberId, communityId);
    }

    @CheckCommunityAdmin(communityIdParam = "communityId", userParam = "user")
    public void removeBlockMember(Long communityId, Long memberId, User user){
        if (memberId == null){
            throw new BadRequestException("memberId could not be null");
        }

        banUtils.removeBanRecord(memberId, BanType.COMMUNITY , communityId);

        LOG.info("{} unbanned user with id {} from his/her community {}", user.getFirstName() + " " + user.getLastName(),
                memberId, communityId);
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
