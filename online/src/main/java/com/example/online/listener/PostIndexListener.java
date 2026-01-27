package com.example.online.listener;

import com.example.online.elasticsearch.service.IndexService;
import com.example.online.event.PostChangedEvent;
import com.example.online.event.PostDeletedEvent;
import com.example.online.helper.Indices;
import com.example.online.listener.service.PostIndexService;
import com.example.online.post.dto.PostGetResponse;
import com.example.online.post.elasticHelper.BuildPostElasticDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PostIndexListener {
    private final PostIndexService postIndexService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void onPostChanged(PostChangedEvent event) {
        postIndexService.indexPost(event);
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void onPostDeleted(PostDeletedEvent event) {
        postIndexService.dropPost(event);
    }
}

