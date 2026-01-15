package com.example.online.domain.model;

import com.example.online.enumerate.RequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter        // sinh getter cho tất cả field
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "request_course_to_post")
public class RequestAttachCourseToPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private User reviewedBy;

    private LocalDateTime sendAt;

    private LocalDateTime reviewedAt;

    private String reasonReject;
}
