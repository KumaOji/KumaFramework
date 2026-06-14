package com.kuma.boot.ai.model;

/**
 * 知识库中某来源下的单个段落（chunk），用于管理界面下钻预览切分结果。
 *
 * @param source 所属来源
 * @param index  在该来源结果中的序号（从 0 开始，仅用于展示，非存储顺序保证）
 * @param text   段落文本内容
 */
public record RagSegment(String source, int index, String text) {
}
