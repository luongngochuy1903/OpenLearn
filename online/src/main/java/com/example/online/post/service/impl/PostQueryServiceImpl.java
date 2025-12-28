package com.example.online.post.service.impl;

import com.example.online.course.dto.CourseGetResponse;
import com.example.online.coursemodule.service.CourseModuleService;
import com.example.online.domain.model.Community;
import com.example.online.domain.model.Course;
import com.example.online.domain.model.Post;
import com.example.online.domain.model.User;
import com.example.online.enumerate.ContributorRole;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.exception.UnauthorizedException;
import com.example.online.post.dto.PostGetResponse;
import com.example.online.post.service.PostQueryService;
import com.example.online.postcourse.service.PostCourseService;
import com.example.online.repository.PostRepository;
import com.example.online.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostQueryServiceImpl implements PostQueryService {
    private final PostCourseService postCourseService;
    private final PostRepository postRepository;
    private final CourseModuleService courseModuleService;

    /*
    Function: Get post thumbnail with the post content, the creator and contributors, community where it
    belongs to or not and courses detached of this user
    Note: (Tree level ends at Course, does not response module and lesson)
     */
    public List<PostGetResponse> viewMyPostDetail(){
        var user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new UnauthorizedException("You need to login first");
        }
        List<Post> posts = postCourseService.getPostByUser(user);
        return posts.stream().map(post -> viewPostDetail(post.getId())).toList();
    }

    /*
    Function: Get post thumbnail with the post content, the creator and contributors, community where it
    belongs to or not and courses detached
    Note: (Tree level ends at Course, does not response module and lesson)
     */
    public PostGetResponse viewPostDetail(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        User creator = postCourseService.getRoleOfPost(post, ContributorRole.CREATOR).get(0);
        List<User> contributors = postCourseService.getRoleOfPost(post, ContributorRole.CONTRIBUTOR);
        List<String> contributors_name = contributors.stream().map(user -> user.getLastName() + user.getFirstName()).toList();
        //Find course with post
        List<Course> courses = postCourseService.getCourseByPost(post);
        List<CourseGetResponse> courseGetResponses = courses.stream().map(course -> {
            Set<String> tags_name = course.getTags().stream().map(tag -> tag.getName()).collect(Collectors.toSet());
            User courseCreator = courseModuleService.getRoleOfCourse(course, ContributorRole.CREATOR).get(0);
            if (courseCreator.getLastName() == null || courseCreator.getFirstName() == null){
                throw new ResourceNotFoundException("User name not found");
            }

            return CourseGetResponse.builder().id(course.getId()).courseName(course.getName())
                    .creatorName(courseCreator.getFirstName() + " " + courseCreator.getLastName()).creatorId(courseCreator.getId())
                    .description(course.getDescription()).tagName(tags_name).build();
        }).toList();

        // Find Community by Post
        Community community = post.getCommunity();
        if (community == null){
            throw new ResourceNotFoundException("Community not found!");
        }
        String communityName = community.getName();
        Long communityId = community.getId();

        return PostGetResponse.builder().postId(post.getId()).name(post.getName())
                .contentMarkdown(post.getContentMarkdown()).updateAt(post.getUpdateAt()).creatorId(creator.getId())
                .creator(creator.getFirstName() + " " + creator.getLastName()).contributors(contributors_name)
                .communityName(communityName).communityId(communityId).courseGetResponses(courseGetResponses).build();
    }
}
