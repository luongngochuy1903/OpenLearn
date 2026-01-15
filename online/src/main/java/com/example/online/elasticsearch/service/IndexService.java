package com.example.online.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.example.online.exception.ElasticSearchIndexException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IndexService {
    private final ElasticsearchClient client;
    private static final Logger LOG = LoggerFactory.getLogger(IndexService.class);

    /*
    Function: Upsert new document for index
     */
    public <T> void upsertDocument(T document, String id, String indices){
        try{
            client.index(i -> i.index(indices)
                    .id(id).document(document)
            );
        }
        catch (Exception e){
            LOG.error("Something wrong when upserting document with id {} to index {}: {}", id,
                    indices, e.getMessage());
            throw new ElasticSearchIndexException("Failed to upsert document into Elasticsearch", e);
        }
    }

    /*
    Function: Delete document
     */
    public void deleteDocument(String id, String indices){
        try{
            client.delete(d -> d.index(indices).id(id));
        }
        catch (Exception e){
            LOG.error("Something wrong when deleting document with id {} of index {}: {}", id,
                    indices, e.getMessage());
            throw new ElasticSearchIndexException("Failed to delete document in Elasticsearch", e);
        }
    }
}
