package com.example.online.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "post_comment")
public class Comment {
    @Id
    private String id;

    private Long postId;
    private String content;

    @Indexed
    private String parentId;

    private LocalDateTime createdAt;
}
