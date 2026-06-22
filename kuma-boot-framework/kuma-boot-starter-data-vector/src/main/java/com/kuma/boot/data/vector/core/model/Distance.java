package com.kuma.boot.data.vector.core.model;

/**
 * 向量相似度 / 距离度量方式。
 *
 * <p>不同向量库底层支持的度量略有差异，这里取三种最通用的语义，由各实现映射到自身的取值。
 */
public enum Distance {

    /** 余弦相似度，取值越大越相似（RAG 场景最常用） */
    COSINE,

    /** 欧氏距离，原始距离越小越相似（实现统一归一化为「越大越相似」的 score） */
    EUCLID,

    /** 点积，取值越大越相似 */
    DOT
}
