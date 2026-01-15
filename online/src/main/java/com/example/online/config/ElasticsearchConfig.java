package com.example.online.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class ElasticsearchConfig {

    @Value("${app.elasticsearch.url}")
    private String elasticsearchUrl;


    //Note: Lack of setting authentication configuration => waiting for production deploy
    @Bean
    public ElasticsearchClient elasticsearchClient(ObjectMapper objectMapper) {
        URI uri = URI.create(elasticsearchUrl);
        RestClient restClient = RestClient.builder(
                new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme())
        ).build();

        JacksonJsonpMapper jacksonJsonpMapper = new JacksonJsonpMapper(objectMapper);

        RestClientTransport transport = new RestClientTransport(restClient, jacksonJsonpMapper);

        return new ElasticsearchClient(transport);
    }
}

