package com.kuma.boot.ai.service.impl;

import com.kuma.boot.ai.service.AiEmbeddingService;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.CosineSimilarity;

public class AiEmbeddingServiceImpl implements AiEmbeddingService {

    private final EmbeddingModel embeddingModel;

    public AiEmbeddingServiceImpl(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Override
    public float[] embed(String text) {
        return embeddingModel.embed(text).content().vector();
    }

    @Override
    public double cosineSimilarity(String text1, String text2) {
        Embedding emb1 = embeddingModel.embed(text1).content();
        Embedding emb2 = embeddingModel.embed(text2).content();
        return CosineSimilarity.between(emb1, emb2);
    }
}
