package com.example.online.module.controller;

import com.example.online.module.dto.ModuleCreateRequest;
import com.example.online.module.dto.ModuleCreateResponse;
import com.example.online.domain.model.Module;
import com.example.online.module.service.ModuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
public class ModuleController {
    private final ModuleService moduleService;

    @PostMapping
    public ResponseEntity<ModuleCreateResponse> createModules(@Valid @RequestBody ModuleCreateRequest moduleCreateRequest){
        Module module = moduleService.createModule(moduleCreateRequest);
        ModuleCreateResponse moduleCreateResponse = ModuleCreateResponse.builder()
                .moduleId(module.getId()).message("Create module successfully").build();
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleCreateResponse);
    }

    @DeleteMapping("/{moduleId}")
    public ResponseEntity<String> deleteModules(@PathVariable Long moduleId){
        moduleService.deleteModule(moduleId);
        return ResponseEntity.ok("Delete module successfully");
    }
}
