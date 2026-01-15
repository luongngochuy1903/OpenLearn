package com.example.online.module.service;

import com.example.online.domain.model.User;
import com.example.online.module.dto.ModuleCreateRequest;
import com.example.online.domain.model.Module;
import com.example.online.module.dto.ModuleUpdateRequest;

public interface ModuleService {
    Module createModule(ModuleCreateRequest moduleCreateRequest, User user);
    Module saveModule(Module module);
    Module updateModule(Long moduleId, ModuleUpdateRequest moduleUpdateRequest, User user);
    void deleteModule(Long moduleId, User user);
}
