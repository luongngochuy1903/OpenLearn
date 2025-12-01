package com.example.online.service;

import com.example.online.DTO.ModuleCreateRequest;
import com.example.online.model.Module;

public interface ModuleService {
    Module createModule(ModuleCreateRequest moduleCreateRequest);
    void saveModule(Module module);
}
