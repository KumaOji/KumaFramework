package com.kuma.boot.ai.service;

public interface AiEmbeddingService {

    /** 将文本编码为向量 */
    float[] embed(String text);

    /** 计算两段文本的余弦相似度，返回值在 [-1, 1] 之间 */
    double cosineSimilarity(String text1, String text2);
}
