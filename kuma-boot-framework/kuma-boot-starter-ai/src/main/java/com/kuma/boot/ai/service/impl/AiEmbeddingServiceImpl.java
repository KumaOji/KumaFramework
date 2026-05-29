package com.kuma.boot.ai.service.impl;

import com.kuma.boot.ai.service.AiEmbeddingService;
import dev.langchain4j.model.embedding.EmbeddingModel;

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
        return cosine(embed(text1), embed(text2));
    }

    private static double cosine(float[] a, float[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot   += (double) a[i] * b[i];
            normA += (double) a[i] * a[i];
            normB += (double) b[i] * b[i];
        }
        double denom = Math.sqrt(normA) * Math.sqrt(normB);
        return denom == 0 ? 0 : dot / denom;
    }
}
