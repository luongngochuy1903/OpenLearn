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
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToMany
    @JoinTable(name = "course_tag", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostCourse> postCourses = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CourseModule> courseModules = new HashSet<>();


    private String commentURL;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

}
