package com.example.online.repository;

import com.example.online.domain.model.RequestAttachModuleToCourse;
import com.example.online.enumerate.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequestAttachModuleToCourseRepository extends JpaRepository<RequestAttachModuleToCourse, Long> {
    Page<RequestAttachModuleToCourse> findAllByCourse_IdAndStatusAndModuleIsNotNull(Long courseId, RequestStatus status, Pageable pageable);
    Optional<RequestAttachModuleToCourse> findByCourse_IdAndModule_IdAndStatus(Long courseId, Long moduleId, RequestStatus status);
}
