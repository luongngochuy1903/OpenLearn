package com.example.online.post.service.impl;

import com.example.online.annotation.CheckPostCreator;
import com.example.online.document.factory.DocumentGenerateFactory;
import com.example.online.document.service.DocumentService;
import com.example.online.domain.model.*;
import com.example.online.enumerate.DocumentOf;
import com.example.online.enumerate.ESType;
import com.example.online.enumerate.OutboxEventType;
import com.example.online.enumerate.OutboxStatus;
import com.example.online.exception.BadRequestException;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.exception.UnauthorizedException;
import com.example.online.post.dto.PostUpdateRequest;
import com.example.online.post.service.PostService;
import com.example.online.repository.OutboxRepository;
import com.example.online.repository.PostRepository;
import com.example.online.worker.OutboxWorker;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final DocumentGenerateFactory documentGenerateFactory;
    private final OutboxRepository outboxRepository;
    private final OutboxWorker outboxWorker;
    private static final Logger LOG = LoggerFactory.getLogger(PostServiceImpl.class);

    @Transactional
    @CheckPostCreator(postIdParam = "postId", userParam = "user")
    public void deletePost(Long postId, User user){
        if (user == null) {
            throw new UnauthorizedException("You need to login first");
        }
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found !"));
        postRepository.delete(post);
        //check coi bảng document có bị xóa cascade không

        OutBoxEvent outBoxEvent = outboxWorker.getOutBoxEvent(postId, ESType.POST, List.of(OutboxStatus.NEW, OutboxStatus.FAILED, OutboxStatus.PROCESSING));
        if (outBoxEvent != null) {
            if (outBoxEvent.getStatus().equals(OutboxStatus.PROCESSING)) {
                throw new BadRequestException("Something happened! Please try again later");
            }
            outBoxEvent.setStatus(OutboxStatus.NEW);
            outBoxEvent.setEventType(OutboxEventType.DELETED);
        }
        else{
            outBoxEvent = OutBoxEvent.builder()
                    .aggregateId(postId)
                    .eventType(OutboxEventType.DELETED)
                    .type(ESType.POST)
                    .status(OutboxStatus.NEW)
                    .createdAt(Instant.now())
                    .build();
        }
        outboxRepository.save(outBoxEvent);

        LOG.info("User {} deleted post {}", user.getFirstName() + " " + user.getLastName(), postId);
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
        if (postUpdateRequest.getAddDocs() != null && !postUpdateRequest.getAddDocs().isEmpty()){
            DocumentService documentService = documentGenerateFactory.getService(DocumentOf.POST);
            List<?> results = documentService.resolveDocument(postUpdateRequest.getAddDocs(), authUser);
            @SuppressWarnings("unchecked")
            List<PostDocument> postDocs = (List<PostDocument>) results;
            for (var doc : postDocs){
                post.getDocumentURL().add(doc);
            }
        }

        if (postUpdateRequest.getRemoveDocs() != null && !postUpdateRequest.getRemoveDocs().isEmpty()){
            DocumentService documentService = documentGenerateFactory.getService(DocumentOf.POST);
            List<?> results = documentService.resolveDocument(postUpdateRequest.getRemoveDocs(), authUser);
            @SuppressWarnings("unchecked")
            List<PostDocument> postDocs = (List<PostDocument>) results;
            for (var doc : postDocs){
                post.getDocumentURL().remove(doc);
            }
        }
        post.setUpdateAt(LocalDateTime.now());
        Post savedPost = postRepository.save(post);

        OutBoxEvent outBoxEvent = outboxWorker.getOutBoxEvent(postId, ESType.POST, List.of(OutboxStatus.NEW, OutboxStatus.FAILED, OutboxStatus.PROCESSING));
        if (outBoxEvent != null) {
            if (outBoxEvent.getStatus().equals(OutboxStatus.PROCESSING)) {
                throw new BadRequestException("Something happened! Please try again later");
            }
            outBoxEvent.setStatus(OutboxStatus.NEW);
            outBoxEvent.setEventType(OutboxEventType.CHANGED);
        }
        else{
            outBoxEvent = OutBoxEvent.builder()
                    .aggregateId(postId)
                    .eventType(OutboxEventType.CHANGED)
                    .type(ESType.POST)
                    .status(OutboxStatus.NEW)
                    .createdAt(Instant.now())
                    .build();
        }
        outboxRepository.save(outBoxEvent);
        LOG.info("User {} updated post {}", authUser.getFirstName() + " " + authUser.getLastName(), postId);
        return savedPost;
    }
}
