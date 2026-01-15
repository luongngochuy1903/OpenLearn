package com.example.online.post.elasticHelper;

import com.example.online.domain.model.Community;
import com.example.online.domain.model.Post;
import com.example.online.domain.model.User;
import com.example.online.enumerate.ContributorRole;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.post.dto.PostGetResponse;
import com.example.online.postcourse.service.PostCourseService;
import com.example.online.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildPostElasticDocument {
    private final PostCourseService postCourseService;
    private final PostRepository postRepository;
    private static final Logger LOG = LoggerFactory.getLogger(BuildPostElasticDocument.class);

    public PostGetResponse getPostDocument(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        User creator = postCourseService.getRoleOfPost(post, ContributorRole.CREATOR).get(0);
        List<User> contributors = postCourseService.getRoleOfPost(post, ContributorRole.CONTRIBUTOR);
        List<String> contributors_name = contributors.stream().map(user -> user.getLastName() + user.getFirstName()).toList();

        // Find Community by Post
        Community community = post.getCommunity();
        Long communityId = null;
        String communityName = null;
        if (community != null) {
            communityName = community.getName();
            communityId = community.getId();
        }
        LOG.info("Built document for post {}", post.getId());
        return PostGetResponse.builder().postId(post.getId()).name(post.getName())
                .contentMarkdown(post.getContentMarkdown()).updateAt(post.getUpdateAt()).creatorId(creator.getId())
                .creator(creator.getFirstName() + " " + creator.getLastName()).contributors(contributors_name)
                .communityName(communityName).communityId(communityId).build();
    }
}
