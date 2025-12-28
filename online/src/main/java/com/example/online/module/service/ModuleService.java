package com.example.online.module.service;

import com.example.online.module.dto.ModuleCreateRequest;
import com.example.online.domain.model.Module;

public interface ModuleService {
    Module createModule(ModuleCreateRequest moduleCreateRequest);
    void saveModule(Module module);
    void deleteModule(Long moduleId);
}
