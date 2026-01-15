package com.example.online.elasticsearch.init;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.helper.Indices;
import com.example.online.utils.LoadingUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.util.List;


@Service
@RequiredArgsConstructor
public class IndexInitialize {
    private static final Logger LOG = LoggerFactory.getLogger(IndexInitialize.class);
    private static final List<String> indices = List.of(Indices.POST_INDEX, Indices.COURSE_INDEX);
    private final ElasticsearchClient client;

    /*
    Function: Every time rebooting program, this function should be initialized. This function load setting
    file and document structure file (JSON) and recreating its configuration in ES if exists, so that an
    empty index is ready.
     */
    @PostConstruct
    public void recreatingIndex(){
        recreatingIndices();
    }

    public void recreatingIndices(){
        try{
            String settings = LoadingUtils.loadAsString("static/es-settings.json");
            if(settings == null){
                LOG.error("Fail to load settings file");
                throw new IllegalStateException("Cannot load Elasticsearch settings");
            }

            for(String indexName : indices){
                ExistsRequest request = new ExistsRequest.Builder().index(indexName).build();
                boolean exists = client.indices().exists(request).value();

                if(exists){
                    client.indices().delete(e -> e.index(indexName));
                    LOG.info("Deleted index {}", indexName);
                }

                String mappings = loadMappings(indexName);
                client.indices().create(i -> i.index(indexName)
                        .mappings(m -> m.withJson(new StringReader(mappings)))
                        .settings(s -> s.withJson(new StringReader(settings)))
                );
                LOG.info("Create index for {}", indexName);
            }
        }
        catch (Exception e){
            LOG.error("Fail when creating indices: {}", e.getMessage());
        }
    }

    private String loadMappings(String indexName){
        String path = LoadingUtils.loadAsString("static/mappings/" + indexName + ".json");
        if(path == null){
            LOG.error("Fail when loading mappings {}", indexName);
            throw new ResourceNotFoundException("Mapping index not found!");
        }
        return path;
    }
}
