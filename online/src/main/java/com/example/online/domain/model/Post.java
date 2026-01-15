package com.example.online.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter        // sinh getter cho tất cả field
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String contentMarkdown;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private Community community;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<PostCourse> postCourses = new HashSet<>();
}
