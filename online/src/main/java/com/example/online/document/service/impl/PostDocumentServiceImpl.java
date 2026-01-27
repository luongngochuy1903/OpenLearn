package com.example.online.document.service.impl;

import com.example.online.document.dto.DocumentRequestDTO;
import com.example.online.document.service.DocumentService;
import com.example.online.domain.model.Post;
import com.example.online.domain.model.PostDocument;
import com.example.online.domain.model.User;
import com.example.online.domain.model.UserDocument;
import com.example.online.enumerate.DocumentOf;
import com.example.online.enumerate.UploadType;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.repository.PostDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PostDocumentServiceImpl implements DocumentService {
    private final PostDocumentRepository postDocumentRepository;
    private static final Logger LOG = LoggerFactory.getLogger(PostDocumentServiceImpl.class);

    public DocumentOf getTypes(){ return DocumentOf.POST; };

    @Transactional
    public PostDocument createDocument(Object object, DocumentRequestDTO dto){
        Post post = (Post) object;
        PostDocument postDocument = PostDocument.builder().post(post)
                .url(dto.getObjectKey()).type(dto.getType()).build();
        return postDocumentRepository.save(postDocument);
    }

    @Transactional
    public PostDocument createDocument(Object object, UploadType type, String url){
        Post post = (Post) object;
        PostDocument postDocument = PostDocument.builder().post(post)
                .url(url).type(type).build();
        return postDocumentRepository.save(postDocument);
    }

    public PostDocument updateDocument(Object object, DocumentRequestDTO dto){
        PostDocument postDocument = postDocumentRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        return postDocumentRepository.save(postDocument);
    }

    public void deleteDocument(Object object, String objectKey){
        Post post = (Post) object;
        PostDocument postDocument = postDocumentRepository.findByPostAndUrl(post, objectKey)
                .orElseThrow(() -> new ResourceNotFoundException("document not found in post"));
        postDocumentRepository.delete(postDocument);
    }

    public void deleteDocument(Long id){
        PostDocument postDocument = postDocumentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("document not found in user"));
        postDocumentRepository.delete(postDocument);
    }

    public List<PostDocument> resolveDocument(List<DocumentRequestDTO> docs, User user) {
        LOG.info("Documents {} are created", docs);
        return docs.stream()
                .map(req ->
                        postDocumentRepository.findByUrl(req.getObjectKey())
                                .orElseGet(() -> postDocumentRepository.save(
                                        PostDocument.builder().url(req.getObjectKey())
                                                .type(req.getType())
                                                .build()
                                ))
                )
                .toList();
    }
}
