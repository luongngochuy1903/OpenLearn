package com.example.online.domain.model;

import com.example.online.enumerate.ContributorRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter        // sinh getter cho tất cả field
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "course_module")
public class CourseModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private Integer suggested;

    @Enumerated(EnumType.STRING)
    private ContributorRole role;

    private LocalDateTime createdAt;
}
