package com.example.online.post.service.impl;

import com.example.online.annotation.CheckPostCreator;
import com.example.online.document.factory.DocumentGenerateFactory;
import com.example.online.document.service.DocumentService;
import com.example.online.domain.model.LessonDocument;
import com.example.online.domain.model.Post;
import com.example.online.domain.model.PostDocument;
import com.example.online.domain.model.User;
import com.example.online.enumerate.DocumentOf;
import com.example.online.event.PostChangedEvent;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.exception.UnauthorizedException;
import com.example.online.post.dto.PostUpdateRequest;
import com.example.online.post.service.PostService;
import com.example.online.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final DocumentGenerateFactory documentGenerateFactory;
    private final ApplicationEventPublisher publisher;

    @CheckPostCreator(postIdParam = "postId", userParam = "user")
    public void deletePost(Long postId, User user){
        if (user == null) {
            throw new UnauthorizedException("You need to login first");
        }
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found !"));
        postRepository.delete(post);
        //check coi bảng document có bị xóa cascade không

        publisher.publishEvent(new PostChangedEvent(postId));
        //Thêm xóa comment related
    }

    //Function: Modifying own post
    @CheckPostCreator(postIdParam = "postId", userParam = "authUser")
    public Post updateMyPost(Long postId, PostUpdateRequest postUpdateRequest, User authUser){
        if (authUser == null) {
            throw new UnauthorizedException("You need to login first");
        }
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (postUpdateRequest.getName() != null){
            post.setName(postUpdateRequest.getName());
        }
        if(postUpdateRequest.getContentMarkdown() != null){
            post.setContentMarkdown(postUpdateRequest.getContentMarkdown());
        }
        if (postUpdateRequest.getDocs() != null && !postUpdateRequest.getDocs().isEmpty()){
            DocumentService documentService = documentGenerateFactory.getService(DocumentOf.POST);
            List<?> results = documentService.resolveDocument(postUpdateRequest.getDocs());
            @SuppressWarnings("unchecked")
            List<PostDocument> lessonDocs = (List<PostDocument>) results;
            post.setDocumentURL(lessonDocs);
        }
        post.setUpdateAt(LocalDateTime.now());
        Post savedPost = postRepository.save(post);
        publisher.publishEvent(new PostChangedEvent(postId));
        return savedPost;
    }
}
