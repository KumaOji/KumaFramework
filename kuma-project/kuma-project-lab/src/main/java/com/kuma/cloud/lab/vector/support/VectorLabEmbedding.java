package com.kuma.cloud.lab.vector.support;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 实验用确定性伪向量生成器。
 *
 * <p>不依赖外部 embedding 服务，通过文本哈希映射到固定维度向量，便于本地验证
 * {@link com.kuma.boot.data.vector.core.VectorStore} 的写入与检索流程。
 */
public final class VectorLabEmbedding {

    private VectorLabEmbedding() {
    }

    /**
     * 将文本映射为指定维度的归一化向量。
     */
    public static float[] embed(String text, int dimension) {
        if (dimension <= 0) {
            throw new IllegalArgumentException("dimension 必须大于 0");
        }
        float[] vector = new float[dimension];
        if (text == null || text.isBlank()) {
            return vector;
        }
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < bytes.length; i++) {
            int index = (bytes[i] & 0xFF) % dimension;
            vector[index] += (bytes[i] & 0xFF) / 255.0f;
        }
        normalize(vector);
        return vector;
    }

    /**
     * 将 List&lt;Float&gt; 转为 float[]，并校验维度。
     */
    public static float[] toArray(java.util.List<Float> values, int expectedDimension) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("vector 不能为空");
        }
        if (values.size() != expectedDimension) {
            throw new IllegalArgumentException("vector 维度应为 " + expectedDimension + "，实际为 " + values.size());
        }
        float[] array = new float[values.size()];
        for (int i = 0; i < values.size(); i++) {
            array[i] = values.get(i);
        }
        return array;
    }

    /**
     * 将 float[] 转为 List&lt;Float&gt;，便于 JSON 序列化展示。
     */
    public static java.util.List<Float> toList(float[] vector) {
        if (vector == null) {
            return java.util.List.of();
        }
        Float[] boxed = new Float[vector.length];
        for (int i = 0; i < vector.length; i++) {
            boxed[i] = vector[i];
        }
        return Arrays.asList(boxed);
    }

    private static void normalize(float[] vector) {
        double norm = 0;
        for (float value : vector) {
            norm += (double) value * value;
        }
        if (norm == 0) {
            return;
        }
        double sqrt = Math.sqrt(norm);
        for (int i = 0; i < vector.length; i++) {
            vector[i] = (float) (vector[i] / sqrt);
        }
    }

}
