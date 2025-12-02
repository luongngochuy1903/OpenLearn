package com.example.online.service.impl;

import com.example.online.DTO.ModuleCreateRequest;
import com.example.online.model.Module;
import com.example.online.repository.ModuleRepository;
import com.example.online.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {
    private final ModuleRepository moduleRepository;

    public Module createModule(ModuleCreateRequest moduleCreateRequest){
        return Module.builder().name(moduleCreateRequest.getName()).description(moduleCreateRequest.getDescription()).courseModules(new HashSet<>())
                .lessons(new HashSet<>()).createdAt(LocalDateTime.now()).updateAt(LocalDateTime.now()).build();

    }

    public void saveModule(Module module){
        moduleRepository.save(module);
    }
}
