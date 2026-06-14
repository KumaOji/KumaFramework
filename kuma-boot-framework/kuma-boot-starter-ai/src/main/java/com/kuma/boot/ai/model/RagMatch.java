package com.kuma.boot.ai.model;

/**
 * 检索测试命中的一个段落及其相似度，用于管理界面调参（topK / minScore / chunkSize）。
 *
 * @param source 命中段落所属来源
 * @param text   段落文本内容
 * @param score  相似度分数（0~1，越大越相关）
 */
public record RagMatch(String source, String text, double score) {
}
