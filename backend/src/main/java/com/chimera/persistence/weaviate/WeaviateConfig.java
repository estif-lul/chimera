package com.chimera.persistence.weaviate;

import io.weaviate.client.Config;
import io.weaviate.client.WeaviateClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Weaviate client configuration for mutable long-term agent memory collections.
 */
@Configuration
public class WeaviateConfig {

    @Value("${chimera.weaviate.url:http://localhost:8081}")
    private String weaviateUrl;

    @Bean
    public WeaviateClient weaviateClient() {
        Config config = new Config("http", weaviateUrl.replaceFirst("^https?://", ""));
        return new WeaviateClient(config);
    }
}
