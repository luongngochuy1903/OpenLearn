package com.example.online.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
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
    private String ContentURL;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<PostCourse> postCourses = new HashSet<>();
}
