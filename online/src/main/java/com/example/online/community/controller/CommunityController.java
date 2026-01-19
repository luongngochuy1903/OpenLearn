package com.example.online.community.controller;

import com.example.online.annotation.CurrentUser;
import com.example.online.community.dto.CommunityCreateRequest;
import com.example.online.community.dto.CommunityCreateResponse;
import com.example.online.community.dto.MemberRequestResponse;
import com.example.online.community.service.CommunityAdminService;
import com.example.online.community.service.CommunityService;
import com.example.online.community.service.CommunityMemberService;
import com.example.online.domain.model.*;
import com.example.online.post.dto.PostCreateRequest;
import com.example.online.post.enumerate.PostCreateType;
import com.example.online.post.factory.PostCreateFactory;
import com.example.online.postcourse.dto.PostCreateResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/communities")
public class CommunityController {
    private final CommunityService communityService;
    private final CommunityAdminService communityAdminService;
    private final CommunityMemberService communityMemberService;
    private final PostCreateFactory postCreateFactory;
    private static final Logger LOG = LoggerFactory.getLogger(CommunityController.class);


    // Role: Admin, User Use-case Role: Community Admin
    @PostMapping
    public ResponseEntity<CommunityCreateResponse> createCommunity(@RequestBody CommunityCreateRequest communityCreateRequest,
                                                                   @CurrentUser User user){
        Community community = communityService.createCommunity(communityCreateRequest, user);
        CommunityCreateResponse response = CommunityCreateResponse.builder()
                .communityId(community.getId()).message(String.format("Community %s is created successfully !", community.getName()))
                .url("http://localhost:5173/test")
                .build();
        LOG.info("POST /api/v1/communities - Body: CommunityCreateRequest");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Role: Admin, User Use-case Role: Community Admin
    @PutMapping("/{communityId}")
    public ResponseEntity<CommunityCreateResponse> updateCommunity(@PathVariable Long communityId,
                                                                   @RequestBody CommunityCreateRequest communityCreateRequest,
                                                                   @CurrentUser User user){
        Community community = communityService.updateCommunity(communityCreateRequest, communityId, user);
        CommunityCreateResponse communityCreateResponse = CommunityCreateResponse.builder()
                .communityId(community.getId()).message("update community information successfully")
                .url("http://localhost:5173/test").build();
        LOG.info("PUT /api/v1/communities/{} - Body: CommunityCreateRequest", communityId);
        return ResponseEntity.ok(communityCreateResponse);
    }

    // Role: Admin, User Use-case Role: Community Admin
    @DeleteMapping("/{communityId}")
    public ResponseEntity<String> deleteCommunity(@PathVariable Long communityId, @CurrentUser User user){
        communityService.deleteCommunity(communityId, user);
        LOG.info("DELETE /api/v1/communities/{} - Body: CommunityCreateRequest", communityId);
        return ResponseEntity.ok("community deleted successfully");
    }

    //==========================================================================
    // Role: Admin, User Use-case Role: User
    @PostMapping("/{communityId}/members/request")
    public ResponseEntity<String> sendJoiningCommunityRequest(@PathVariable Long communityId, @CurrentUser User user){
        RequestJoiningCommunity req = communityMemberService.sendJoinCommunityRequest(communityId, user);
        return ResponseEntity.ok("Send joining request successfully");
    }

    // Role: Admin, User Use-case Role: Community Admin
    @PostMapping("/{communityId}/members/{memberId}/approved")
    public ResponseEntity<String> approveMember(@PathVariable Long communityId, @PathVariable Long memberId, @CurrentUser User user){
        CommunityMember communityMember = communityAdminService.approveCommunityMember(communityId, memberId, user);
        LOG.info("POST /api/v1/communities/{}/members/{}/approved - Body: CommunityCreateRequest", communityId, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(String.format("User %s is added to community!", communityMember.getUser().getEmail()));
    }

    // Role: Admin, User Use-case Role: Community Admin
    @PostMapping("/{communityId}/members/{memberId}/declined")
    public ResponseEntity<String> declineMember(@PathVariable Long communityId, @PathVariable Long memberId, @CurrentUser User user){
        communityAdminService.declineCommunityMember(communityId, memberId, user);
        LOG.info("DELETE /api/v1/communities/{}/members/{}/declined - Body: Null", communityId, memberId);
        return ResponseEntity.ok().body(String.format("User with id %s is declined from joining community!", memberId));
    }

    // Role: Admin, User Use-case Role: Community Admin
    @PostMapping("/{communityId}/members/{memberId}/blocked")
    public ResponseEntity<String> blockMember(@PathVariable Long communityId, @PathVariable Long memberId, @CurrentUser User user){
        communityAdminService.blockMember(communityId, memberId, user);
        LOG.info("POST /api/v1/communities/{}/members/{}/blocked - Body: Null", communityId, memberId);
        return ResponseEntity.ok().body(String.format("User with id %s is blocked from joining community!", memberId));
    }

    @DeleteMapping("/{communityId}/members/{memberId}/unblocked")
    public ResponseEntity<String> unblockMember(@PathVariable Long communityId, @PathVariable Long memberId, @CurrentUser User user){
        communityAdminService.removeBlockMember(communityId, memberId, user);
        LOG.info("POST /api/v1/communities/{}/members/{}/unblocked - Body: Null", communityId, memberId);
        return ResponseEntity.ok().body(String.format("User with id %s is unblocked from joining community!", memberId));
    }

    // Role: Admin, User Use-case Role: Community Admin
    @GetMapping("/{communityId}")
    public ResponseEntity<Page<MemberRequestResponse>> getJoiningRequest(@PathVariable Long communityId, Pageable pageable, @CurrentUser User user){
        LOG.info("GET /api/v1/communities/{} - Body: NULL", communityId);
        return ResponseEntity.ok(communityAdminService.getRequestJoiningTable(communityId, pageable, user));
    }

    //========================== Modify post in community =================================
    @PostMapping("/{communityId}/posts/{types}")
    public ResponseEntity<PostCreateResponse> createPostInCommunity(@PathVariable Long communityId,
                                                                      @PathVariable("types") PostCreateType postCreateType,
                                                                      @RequestBody PostCreateRequest postCreateRequest,
                                                                      @CurrentUser User authUser){
        Post post = postCreateFactory.createInCommunity(communityId, postCreateType, postCreateRequest, authUser);
        PostCreateResponse postCreateResponse = PostCreateResponse.builder().postId(post.getId()).message("Create post successfully")
                .url("http://localhost:5173/test").build();
        return ResponseEntity.status(HttpStatus.CREATED).body(postCreateResponse);
    }
}
