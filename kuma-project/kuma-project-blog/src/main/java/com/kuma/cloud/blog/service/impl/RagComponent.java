package com.kuma.cloud.blog.service.impl;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RagComponent {

    private static final Logger log = LoggerFactory.getLogger(RagComponent.class);

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final RestClient qdrantHttpClient;
    private final String collectionName;
    private final int dimension;

    public RagComponent(
            @Value("${ai-chat.base-url:http://blog-ai-ui:8080}") String aiBaseUrl,
            @Value("${ai-chat.api-key:}") String apiKey,
            @Value("${ai-chat.embedding-model:nomic-embed-text}") String embeddingModelName,
            @Value("${ai-chat.qdrant.host:localhost}") String qdrantHost,
            @Value("${ai-chat.qdrant.grpc-port:6334}") int grpcPort,
            @Value("${ai-chat.qdrant.http-port:6333}") int httpPort,
            @Value("${ai-chat.qdrant.collection:blog-docs}") String collectionName,
            @Value("${ai-chat.qdrant.dimension:768}") int dimension) {

        this.collectionName = collectionName;
        this.dimension = dimension;

        String chatBaseUrl = aiBaseUrl.endsWith("/") ? aiBaseUrl + "api" : aiBaseUrl + "/api";
        String effectiveKey = apiKey.isBlank() ? "no-key" : apiKey;

        this.embeddingModel = OpenAiEmbeddingModel.builder()
                .baseUrl(chatBaseUrl)
                .apiKey(effectiveKey)
                .modelName(embeddingModelName)
                .build();

        this.embeddingStore = QdrantEmbeddingStore.builder()
                .host(qdrantHost)
                .port(grpcPort)
                .collectionName(collectionName)
                .build();

        this.qdrantHttpClient = RestClient.builder()
                .baseUrl("http://" + qdrantHost + ":" + httpPort)
                .build();
    }

    @PostConstruct
    void ensureCollection() {
        try {
            qdrantHttpClient.put()
                    .uri("/collections/" + collectionName)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("vectors", Map.of("size", dimension, "distance", "Cosine")))
                    .retrieve()
                    .toBodilessEntity();
            log.info("Qdrant collection '{}' created (dimension={})", collectionName, dimension);
        } catch (Exception e) {
            log.debug("Qdrant collection '{}': {}", collectionName, e.getMessage());
        }
    }

    public void ingest(String text) {
        List<TextSegment> segments = splitIntoSegments(text);
        if (segments.isEmpty()) return;
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        embeddingStore.addAll(embeddings, segments);
        log.info("Ingested {} segments into collection '{}'", segments.size(), collectionName);
    }

    public String retrieveContext(String query, int maxResults) {
        Embedding queryEmbedding = embeddingModel.embed(query).content();
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(maxResults)
                .build();
        return embeddingStore.search(request).matches().stream()
                .map(m -> m.embedded().text())
                .collect(Collectors.joining("\n\n"));
    }

    private List<TextSegment> splitIntoSegments(String text) {
        return Arrays.stream(text.split("\n\n+"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(TextSegment::from)
                .toList();
    }
}
