package com.kuma.boot.ai.model;

/**
 * 知识库中的一个文档来源（按 {@code source} 元数据聚合）。
 *
 * @param source     来源标识，通常为上传文件名；纯文本写入无来源时为占位名
 * @param segments   该来源在向量库中的段落（chunk）数量
 * @param ingestedAt 写入时间（epoch 毫秒），旧数据可能为 null
 * @param size       原始内容字节数，旧数据可能为 null
 */
public record RagSource(String source, int segments, Long ingestedAt, Long size) {
}
