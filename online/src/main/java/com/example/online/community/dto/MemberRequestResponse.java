package com.example.online.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestResponse {
    private Long memberId;
    private String name;
    private String status;
    private LocalDateTime createdAt;
}
