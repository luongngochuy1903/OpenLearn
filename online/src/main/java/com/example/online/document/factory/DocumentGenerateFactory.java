package com.example.online.document.factory;

import com.example.online.document.dto.DocumentRequestDTO;
import com.example.online.document.service.DocumentService;
import com.example.online.enumerate.DocumentOf;
import com.example.online.exception.BadRequestException;
import com.example.online.post.service.PostCreateService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DocumentGenerateFactory {
    private final Map<DocumentOf, DocumentService> serviceMap;

    public DocumentGenerateFactory(List<DocumentService> documentServices){
        this.serviceMap = documentServices.stream()
                .collect(Collectors.toMap(documentService -> documentService.getTypes(),
                Function.identity()
                ));
    }

    public DocumentService getService(DocumentOf type) {
        DocumentService service = serviceMap.get(type);
        if (service == null) {
            throw new BadRequestException("No DocumentService for type: " + type);
        }
        return service;
    }
}
