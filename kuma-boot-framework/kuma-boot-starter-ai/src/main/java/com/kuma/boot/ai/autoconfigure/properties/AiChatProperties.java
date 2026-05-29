package com.kuma.boot.ai.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = AiChatProperties.PREFIX)
public class AiChatProperties {

    public static final String PREFIX = "kuma.boot.ai";

    private String baseUrl = "http://localhost:11434";
    private String apiKey = "";
    private String model = "llama3";

    @NestedConfigurationProperty
    private Embedding embedding = new Embedding();

    @NestedConfigurationProperty
    private Qdrant qdrant = new Qdrant();

    @Data
    public static class Embedding {
        private String baseUrl = "http://localhost:11434";
        private String model = "nomic-embed-text";
    }

    @Data
    public static class Qdrant {
        private String host = "localhost";
        private int grpcPort = 6334;
        private int httpPort = 6333;
        private String collection = "docs";
        private int dimension = 768;
    }
}
