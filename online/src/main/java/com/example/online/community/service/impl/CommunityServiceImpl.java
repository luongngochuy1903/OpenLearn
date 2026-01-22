package com.example.online.community.service.impl;

import com.example.online.annotation.CheckCommunityAdmin;
import com.example.online.community.dto.CommunityCreateRequest;
import com.example.online.community.service.CommunityMemberService;
import com.example.online.domain.model.User;
import com.example.online.enumerate.CommunityRole;
import com.example.online.exception.BadRequestException;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.exception.UnauthorizedException;
import com.example.online.domain.model.Community;
import com.example.online.domain.model.CommunityMember;
import com.example.online.domain.model.Tag;
import com.example.online.repository.CommunityRepository;
import com.example.online.community.service.CommunityService;
import com.example.online.tag.service.TagService;
import com.example.online.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {
    private final CommunityRepository communityRepository;
    private final CommunityMemberService communityMemberService;
    private final TagService tagService;
    private final UserService userService;
    private static final Logger LOG = LoggerFactory.getLogger(CommunityServiceImpl.class);

    /*
       Function: Create Community
     */
    @Transactional
    public Community createCommunity(CommunityCreateRequest communityCreateRequest, User user){

        if (user == null) {
            throw new UnauthorizedException("You need to login first");
        }
        Set<Tag> tagSet = tagService.resolveTags(communityCreateRequest.getTags());

        Community community = Community.builder().name(communityCreateRequest.getName())
                .description(communityCreateRequest.getDescription()).tags(tagSet)
                .createdAt(LocalDateTime.now()).build();

        communityRepository.save(community);

        CommunityMember communityMember = CommunityMember.builder().community(community)
                .user(user).role(CommunityRole.ADMIN).starContribute(0.0).build();
        communityMemberService.save(communityMember);
        LOG.info("User {} created community {} - {}", user.getEmail(), community.getId(), community.getName());
        return community;
    }

    /*
       Function: Update Community
     */
    @CheckCommunityAdmin(communityIdParam = "communityId", userParam = "user")
    public Community updateCommunity(CommunityCreateRequest communityCreateRequest, Long communityId, User user){
        if (user == null) {
            throw new UnauthorizedException("You need to login first");
        }
        if (communityId == null) {
            throw new BadRequestException("CommunityId could not be null");
        }
        if (communityCreateRequest == null) {
            throw new BadRequestException("communityCreateRequest could not be null");
        }

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community Not found"));
        if (communityCreateRequest.getName() != null){
            community.setName(communityCreateRequest.getName());
        }
        if (communityCreateRequest.getDescription() != null){
            community.setDescription(communityCreateRequest.getDescription());
        }
        //Sửa hàm này bằng cách hiện tag id để tránh find theo Name
        Set<Tag> communityTags = tagService.resolveTags(communityCreateRequest.getTags());
        community.setTags(communityTags);
        LOG.info("User {} updated community {} - {}", user.getEmail(), community.getId(), community.getName());
        return communityRepository.save(community);
    }

    @CheckCommunityAdmin(communityIdParam = "communityId", userParam = "user")
    public void deleteCommunity(Long communityId, User user){
        if (user == null) {
            throw new UnauthorizedException("You need to login first");
        }
        if (communityId == null) {
            throw new BadRequestException("CommunityId could not be null");
        }
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community Not found"));
        LOG.info("User {} deleted community {} - {}", user.getEmail(), community.getId(), community.getName());
        communityRepository.delete(community);
    }

    public Community getCommunity(Long communityId){
        return communityRepository.findById(communityId).orElseThrow(() -> new ResourceNotFoundException("Community not found"));
    }

}
