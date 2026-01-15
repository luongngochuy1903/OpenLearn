package com.example.online.post.factory;

import com.example.online.domain.model.Post;
import com.example.online.domain.model.User;
import com.example.online.elasticsearch.service.IndexService;
import com.example.online.exception.BadRequestException;
import com.example.online.post.dto.PostCreateRequest;
import com.example.online.post.elasticHelper.BuildPostElasticDocument;
import com.example.online.post.enumerate.PostCreateType;
import com.example.online.post.service.PostCreateService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PostCreateFactory {
    private final Map<PostCreateType, PostCreateService> serviceMap;

    public PostCreateFactory(List<PostCreateService> services, BuildPostElasticDocument buildPostElasticDocument,
                             IndexService indexService, ApplicationEventPublisher publisher) {
        this.serviceMap = services.stream()
                .collect(Collectors.toMap(postCreateService -> postCreateService.getType(),
                        Function.identity()
                ));
    }

    public Post create(PostCreateType type, PostCreateRequest postCreateRequest, User authUser){
        PostCreateService postCreateService = serviceMap.get(type);
        if(postCreateService == null){
            throw new BadRequestException("Cannot implement suitable PostCreateService: " + PostCreateService.class);
        }
        return postCreateService.createPost(postCreateRequest, authUser);
    }

    public Post createInCommunity(Long communityId, PostCreateType type, PostCreateRequest postCreateRequest, User authUser){
        PostCreateService postCreateService = serviceMap.get(type);
        if(postCreateService == null){
            throw new BadRequestException("Cannot implement suitable PostCreateService: " + PostCreateService.class);
        }
        return postCreateService.createPost(communityId, postCreateRequest, authUser);
    }
}
