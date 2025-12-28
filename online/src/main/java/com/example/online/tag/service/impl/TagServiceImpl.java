package com.example.online.tag.service.impl;

import com.example.online.domain.model.Tag;
import com.example.online.repository.TagRepository;
import com.example.online.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    public void save(Tag tag){
        tagRepository.save(tag);
    }
}
