package com.kuma.boot.ai.model;

/**
 * 知识库概览统计，用于管理界面仪表盘。
 *
 * @param totalSources  文档来源总数
 * @param totalSegments 段落（chunk）总数
 * @param dimension     向量维度
 * @param status        Qdrant collection 健康状态（green / yellow / red / unavailable 等）
 */
public record RagStats(int totalSources, int totalSegments, int dimension, String status) {
}
