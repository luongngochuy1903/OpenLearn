package com.example.online.tag.service.impl;

import com.example.online.course.service.impl.CourseServiceImpl;
import com.example.online.domain.model.Tag;
import com.example.online.repository.TagRepository;
import com.example.online.tag.dto.TagRequest;
import com.example.online.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private static final Logger LOG = LoggerFactory.getLogger(TagServiceImpl.class);

    public void save(Tag tag){
        tagRepository.save(tag);
    }

    public Set<Tag> resolveTags(Set<TagRequest> tagRequests) {
        LOG.info("Tag {} are created", tagRequests);
        return tagRequests.stream()
                .map(req ->
                        tagRepository.findByName(req.getName())
                                .orElseGet(() -> tagRepository.save(
                                        Tag.builder().name(req.getName()).build()
                                ))
                )
                .collect(Collectors.toSet());
    }
}
