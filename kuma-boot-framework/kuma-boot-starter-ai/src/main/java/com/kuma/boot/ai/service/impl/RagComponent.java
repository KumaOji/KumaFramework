package com.kuma.boot.ai.service.impl;

import com.kuma.boot.ai.autoconfigure.properties.AiChatProperties;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RagComponent {

    private static final Logger log = LoggerFactory.getLogger(RagComponent.class);

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final RestClient qdrantHttpClient;
    private final String collectionName;
    private final int dimension;

    public RagComponent(EmbeddingModel embeddingModel, AiChatProperties properties) {
        AiChatProperties.Qdrant qdrant = properties.getQdrant();

        this.embeddingModel = embeddingModel;
        this.collectionName = qdrant.getCollection();
        this.dimension = qdrant.getDimension();

        this.embeddingStore = QdrantEmbeddingStore.builder()
                .host(qdrant.getHost())
                .port(qdrant.getGrpcPort())
                .collectionName(collectionName)
                .build();

        this.qdrantHttpClient = RestClient.builder()
                .baseUrl("http://" + qdrant.getHost() + ":" + qdrant.getHttpPort())
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
            log.info("Qdrant collection '{}' ready (dimension={})", collectionName, dimension);
        } catch (Exception e) {
            log.debug("Qdrant collection '{}': {}", collectionName, e.getMessage());
        }
    }

    public void ingest(String text) {
        List<TextSegment> segments = split(text);
        if (segments.isEmpty()) return;
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        embeddingStore.addAll(embeddings, segments);
        log.info("Ingested {} segments into '{}'", segments.size(), collectionName);
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

    private List<TextSegment> split(String text) {
        return Arrays.stream(text.split("\n\n+"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(TextSegment::from)
                .toList();
    }
}
