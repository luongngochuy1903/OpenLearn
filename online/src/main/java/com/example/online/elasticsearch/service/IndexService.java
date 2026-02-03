package com.example.online.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import com.example.online.elasticsearch.helper.BulkResult;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IndexService {
    private final ElasticsearchClient client;
    private static final Logger LOG = LoggerFactory.getLogger(IndexService.class);

    /*
    Function: Upsert new document for index
     */
    public <T> BulkResult bulkUpsertDocuments(Map<String, T> docsById, String index) {
        if (docsById == null || docsById.isEmpty()) return new BulkResult(List.of(), new ArrayList<>(docsById.keySet()));
        BulkResponse resp;
        try {
            resp = client.bulk(b -> {
                b.index(index);

                docsById.forEach((id, doc) -> b.operations(op -> op
                        .index(idx -> idx
                                .id(id)
                                .document(doc)
                        )
                ));
                return b;
            });

            if (resp.errors()) {
                resp.items().forEach(item -> {
                    if (item.error() != null) {
                        LOG.error("Bulk upsert post batch failed: index={}, id={}, error={}",
                                item.index(), item.id(), item.error().reason());
                    }
                });
            }

        } catch (Exception e) {
            LOG.error("Bulk upsert post batch failed for index {}: {}", index, e.getMessage());
            return new BulkResult(List.of(), new ArrayList<>(docsById.keySet()));
        }
        List<String> success = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (BulkResponseItem item : resp.items()) {
            if (item.error() == null) success.add(item.id());
            else failed.add(item.id());
        }

        return new BulkResult(success, failed);
    }

    /*
    Function: Delete document
     */
    public BulkResult bulkDeleteDocuments(List<String> ids, String index) {
        if (ids == null || ids.isEmpty()) return new BulkResult(List.of(), ids);
        BulkResponse resp;
        try {
            resp = client.bulk(b -> {
                b.index(index);
                ids.forEach(id -> b.operations(op -> op
                        .delete(d -> d.id(id))
                ));
                return b;
            });

            if (resp.errors()) {
                resp.items().forEach(item -> {
                    if (item.error() != null) {
                        LOG.error("Bulk delete failed: index={}, id={}, error={}",
                                item.index(), item.id(), item.error().reason());
                    }
                });
            }
        } catch (Exception e) {
            LOG.error("Bulk delete post batch failed for index {}: {}", index, e.getMessage());
            return new BulkResult(List.of(), ids);
        }

        List<String> success = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (BulkResponseItem item : resp.items()) {
            if (item.error() == null) success.add(item.id());
            else failed.add(item.id());
        }

        return new BulkResult(success, failed);
    }
}
