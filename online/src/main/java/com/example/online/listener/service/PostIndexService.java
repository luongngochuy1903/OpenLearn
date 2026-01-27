package com.example.online.listener.service;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import com.example.online.elasticsearch.service.IndexService;
import com.example.online.event.PostChangedEvent;
import com.example.online.event.PostDeletedEvent;
import com.example.online.helper.Indices;
import com.example.online.post.dto.PostGetResponse;
import com.example.online.post.elasticHelper.BuildPostElasticDocument;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PostIndexService {
    private final BuildPostElasticDocument buildPostElasticDocument;
    private final IndexService indexService;
    private static final Logger LOG = LoggerFactory.getLogger(PostIndexService.class);

    @Async("indexExecutor")
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
    public void indexPost(PostChangedEvent event){
        StopWatch sw = new StopWatch("indexPost");
        sw.start("Start indexing post");
        PostGetResponse doc = buildPostElasticDocument.getPostDocument(event.postId());
        indexService.upsertDocument(doc, event.postId().toString(), Indices.POST_INDEX);
        sw.stop();
        LOG.info("Indexing post {} took {} ms", event.postId(), sw.getTotalTimeMillis());
    }

    @Async("indexExecutor")
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
    public void dropPost(PostDeletedEvent event){
        StopWatch sw = new StopWatch("unindexPost");
        sw.start("Start unindexing post");
        indexService.deleteDocument(event.postId().toString(), Indices.POST_INDEX);
        sw.stop();
        LOG.info("Unindexing post {} took {} ms", event.postId(), sw.getTotalTimeMillis());
    }
}
