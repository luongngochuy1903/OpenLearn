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
@Table(name = "post_course")
public class PostCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private Integer suggested;

    @Enumerated(EnumType.STRING)
    private ContributorRole role;

    private LocalDateTime createdAt;
}
