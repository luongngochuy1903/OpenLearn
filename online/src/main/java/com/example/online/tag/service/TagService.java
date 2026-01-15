package com.example.online.tag.service;

import com.example.online.domain.model.Tag;
import com.example.online.tag.dto.TagRequest;

import java.util.Set;

public interface TagService {
    void save(Tag tag);
    Set<Tag> resolveTags(Set<TagRequest> tagRequests);
}
