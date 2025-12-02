package com.example.online.service.impl;

import com.example.online.model.Tag;
import com.example.online.repository.TagRepository;
import com.example.online.service.TagService;
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
