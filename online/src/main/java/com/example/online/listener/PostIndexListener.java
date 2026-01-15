package com.example.online.listener;

import com.example.online.elasticsearch.service.IndexService;
import com.example.online.event.PostChangedEvent;
import com.example.online.helper.Indices;
import com.example.online.post.dto.PostGetResponse;
import com.example.online.post.elasticHelper.BuildPostElasticDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PostIndexListener {

    private final BuildPostElasticDocument buildPostElasticDocument;
    private final IndexService indexService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void onPostChanged(PostChangedEvent event) {
        System.out.println("Đã nhận post từ publisher");
        PostGetResponse doc = buildPostElasticDocument.getPostDocument(event.postId());
        System.out.println("Build xong");
        indexService.upsertDocument(doc, event.postId().toString(), Indices.POST_INDEX);
        System.out.println("upsert xong");
    }
}

