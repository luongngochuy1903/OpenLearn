package com.example.online.worker.service;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import com.example.online.elasticsearch.helper.BulkResult;
import com.example.online.elasticsearch.service.IndexService;
import com.example.online.helper.Indices;
import com.example.online.post.dto.PostGetResponse;
import com.example.online.post.elasticHelper.BuildPostElasticDocument;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostIndexService {
    private final BuildPostElasticDocument buildPostElasticDocument;
    private final IndexService indexService;
    private static final Logger LOG = LoggerFactory.getLogger(PostIndexService.class);

//    @Async("indexExecutor")
    @Retryable(retryFor = {
            IOException.class,
            ElasticsearchException.class,
            RuntimeException.class
            },
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 1.5
            )
    )
    public BulkResult indexPost(List<Long> postIds){
        StopWatch sw = new StopWatch("indexPost");
        sw.start("Start indexing post");
        Map<String, PostGetResponse> results = postIds.stream().collect(Collectors.toMap(
                Object::toString,
                buildPostElasticDocument::getPostDocument
        ));
        BulkResult r = indexService.bulkUpsertDocuments(results, Indices.POST_INDEX);
        sw.stop();
        LOG.info("Indexing post batch {} took {} ms", postIds, sw.getTotalTimeMillis());
        return r;
    }

//    @Async("indexExecutor")
    @Retryable(retryFor = {
            IOException.class,
            ElasticsearchException.class,
            RuntimeException.class
    },
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 1.5
            )
    )
    public BulkResult dropPost(List<Long> postId){
        StopWatch sw = new StopWatch("unindexPost");
        sw.start("Start unindexing post");
        BulkResult r = indexService.bulkDeleteDocuments(postId.stream().map(Object::toString).toList(), Indices.POST_INDEX);
        sw.stop();
        LOG.info("Unindexing post batch {} took {} ms", postId, sw.getTotalTimeMillis());
        return r;
    }
}
