package com.example.online.document.service.impl;

import com.example.online.document.dto.DocumentRequestDTO;
import com.example.online.document.service.DocumentService;
import com.example.online.domain.model.Lesson;
import com.example.online.domain.model.LessonDocument;
import com.example.online.domain.model.User;
import com.example.online.domain.model.UserDocument;
import com.example.online.enumerate.DocumentOf;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.repository.LessonDocumentRepository;
import com.example.online.repository.UserDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDocumentServiceImpl implements DocumentService {
    private final UserDocumentRepository userDocumentRepository;
    private static final Logger LOG = LoggerFactory.getLogger(UserDocumentServiceImpl.class);

    public DocumentOf getTypes(){ return DocumentOf.USER; };

    public UserDocument createDocument(Object object, DocumentRequestDTO dto){
        User user = (User) object;
        UserDocument userDocument = UserDocument.builder().user(user)
                .url(dto.getObjectKey()).type(dto.getType()).build();
        user.getDocumentURL().add(userDocument);
        return userDocumentRepository.save(userDocument);
    }

    public void deleteDocument(Object object, String objectKey){
        User user = (User) object;
        UserDocument userDocument = userDocumentRepository.findByUserAndUrl(user, objectKey)
                .orElseThrow(() -> new ResourceNotFoundException("document not found in user"));
        userDocumentRepository.delete(userDocument);
    }

    public List<UserDocument> resolveDocument(List<DocumentRequestDTO> docs) {
        LOG.info("Documents {} are created", docs);
        return docs.stream()
                .map(req ->
                        userDocumentRepository.findById(req.getId())
                                .orElseGet(() -> userDocumentRepository.save(
                                        UserDocument.builder().url(req.getObjectKey())
                                                .type(req.getType())
                                                .build()
                                ))
                )
                .toList();
    }
}
