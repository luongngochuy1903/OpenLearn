package com.example.online.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter        // sinh getter cho tất cả field
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LessonDocument> documentURL;

    private String contentMarkdown;
    private String commentURL;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;

    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

}
