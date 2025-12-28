package com.example.online.community.dto;

import com.example.online.tag.dto.TagRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CommunityCreateRequest {
    private String name;
    private Set<TagRequest> tags;
    private String description;
}
