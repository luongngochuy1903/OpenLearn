package com.example.online.repository;

import com.example.online.domain.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    Optional<Module> findByIdAndCreator_Id(Long moduleId, Long creatorId);
}
