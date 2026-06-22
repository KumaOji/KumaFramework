package com.kuma.boot.data.vector.autoconfigure.properties;

import com.kuma.boot.data.vector.core.model.Distance;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 向量库配置（前缀 {@code kuma.boot.data.vector}）。
 */
@Data
@ConfigurationProperties(prefix = VectorStoreProperties.PREFIX)
public class VectorStoreProperties {

    public static final String PREFIX = "kuma.boot.data.vector";

    /** 是否启用向量库自动装配 */
    private boolean enabled = true;

    /** 向量库类型：memory（内存，默认）/ qdrant */
    private Type type = Type.MEMORY;

    /** 默认集合名，供上层未显式指定时使用 */
    private String collection = "vectors";

    /** 默认向量维度（需与所用 embedding 模型一致） */
    private int dimension = 768;

    /** 相似度度量方式 */
    private Distance distance = Distance.COSINE;

    /** 启动时是否自动创建默认集合 */
    private boolean autoCreateCollection = true;

    @NestedConfigurationProperty
    private Qdrant qdrant = new Qdrant();

    public enum Type {
        MEMORY, QDRANT
    }

    @Data
    public static class Qdrant {
        private String host = "localhost";
        /** Qdrant HTTP 端口 */
        private int httpPort = 6333;
        /** 是否使用 HTTPS */
        private boolean useHttps = false;
        /** API Key（Qdrant Cloud / 开启鉴权时填写） */
        private String apiKey = "";
    }
}
