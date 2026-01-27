package com.example.online.document.service.impl;

import com.example.online.document.dto.DocumentRequestDTO;
import com.example.online.document.service.DocumentService;
import com.example.online.domain.model.User;
import com.example.online.domain.model.UserDocument;
import com.example.online.enumerate.DocumentOf;
import com.example.online.enumerate.UploadType;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.repository.LessonDocumentRepository;
import com.example.online.repository.UserDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDocumentServiceImpl implements DocumentService {
    private final UserDocumentRepository userDocumentRepository;
    private static final Logger LOG = LoggerFactory.getLogger(UserDocumentServiceImpl.class);

    public DocumentOf getTypes(){ return DocumentOf.USER; };

    @Transactional
    public UserDocument createDocument(Object object, DocumentRequestDTO dto){
        User user = (User) object;
        UserDocument userDocument = UserDocument.builder().user(user)
                .url(dto.getObjectKey()).type(dto.getType()).build();
        return userDocumentRepository.save(userDocument);
    }

    @Transactional
    public UserDocument createDocument(Object object, UploadType type, String url){
        User user = (User) object;
        UserDocument userDocument = UserDocument.builder().user(user)
                .url(url).type(type).build();
        return userDocumentRepository.save(userDocument);
    }

    @Override
    @Transactional
    public UserDocument updateDocument(Object object, DocumentRequestDTO dto){
        UserDocument userDocument = userDocumentRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        return userDocumentRepository.save(userDocument);
    }

    public void deleteDocument(Object object, String objectKey){
        User user = (User) object;
        UserDocument userDocument = userDocumentRepository.findByUserAndUrl(user, objectKey)
                .orElseThrow(() -> new ResourceNotFoundException("document not found in user"));
        userDocumentRepository.delete(userDocument);
    }

    public void deleteDocument(Long id){
        UserDocument userDocument = userDocumentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("document not found in user"));
        userDocumentRepository.delete(userDocument);
    }

    public List<UserDocument> resolveDocument(List<DocumentRequestDTO> docs, User user) {
        LOG.info("Documents {} are created", docs);
        return docs.stream()
                .map(req ->
                        userDocumentRepository.findByUrl(req.getObjectKey())
                                .orElseGet(() -> userDocumentRepository.save(
                                        UserDocument.builder().url(req.getObjectKey())
                                                .user(user)
                                                .type(req.getType())
                                                .build()
                                ))
                )
                .toList();
    }
}
