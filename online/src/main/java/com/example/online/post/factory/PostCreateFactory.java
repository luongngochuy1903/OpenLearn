package com.example.online.post.factory;

import com.example.online.domain.model.Post;
import com.example.online.exception.BadRequestException;
import com.example.online.post.dto.PostCreateRequest;
import com.example.online.post.enumerate.PostCreateType;
import com.example.online.post.service.PostCreateService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PostCreateFactory {
    private final Map<PostCreateType, PostCreateService> serviceMap;

    public PostCreateFactory(List<PostCreateService> services) {
        this.serviceMap = services.stream()
                .collect(Collectors.toMap(postCreateService -> postCreateService.getType(),
                        Function.identity()
                ));
    }

    public Post create(PostCreateType type, PostCreateRequest postCreateRequest){
        PostCreateService postCreateService = serviceMap.get(type);
        if(postCreateService == null){
            throw new BadRequestException("Cannot implement suitable PostCreateService: " + PostCreateService.class);
        }
        return postCreateService.createPost(postCreateRequest);
    }
}
