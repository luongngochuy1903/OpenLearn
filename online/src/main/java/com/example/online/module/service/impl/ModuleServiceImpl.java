package com.example.online.module.service.impl;

import com.example.online.module.dto.ModuleCreateRequest;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.domain.model.Module;
import com.example.online.repository.ModuleRepository;
import com.example.online.module.service.ModuleService;
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

    public void deleteModule(Long moduleId){
        Module module = moduleRepository.findById(moduleId).orElseThrow(() -> new ResourceNotFoundException("Module not found !"));
        moduleRepository.delete(module);
    }
}
