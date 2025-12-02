package com.example.online.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    private String documentURL;
    private String contentURL;
    private String commentURL;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;

    private LocalDateTime createdAt;
    private LocalDateTime updateAt;



}
