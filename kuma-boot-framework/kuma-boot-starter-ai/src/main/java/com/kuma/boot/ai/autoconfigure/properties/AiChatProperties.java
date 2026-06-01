package com.kuma.boot.ai.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = AiChatProperties.PREFIX)
public class AiChatProperties {

    public static final String PREFIX = "kuma.boot.ai";

    private String baseUrl = "http://localhost:11434";
    private String apiKey = "";
    private String model = "llama3";

    /** 生成温度（0~2），null 使用模型默认值 */
    private Double temperature;

    /** 最大输出 token 数，null 使用模型默认值 */
    private Integer maxTokens;

    /** HTTP 请求超时（含连接 + 读取） */
    private Duration timeout = Duration.ofSeconds(60);

    @NestedConfigurationProperty
    private Embedding embedding = new Embedding();

    @NestedConfigurationProperty
    private Qdrant qdrant = new Qdrant();

    @NestedConfigurationProperty
    private Rag rag = new Rag();

    @NestedConfigurationProperty
    private Memory memory = new Memory();

    @NestedConfigurationProperty
    private Agent agent = new Agent();

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

    @Data
    public static class Rag {
        /** 检索返回的最大段落数 */
        private int topK = 3;
        /** 最低相似度阈值（0~1），低于此分数的结果不会作为上下文 */
        private double minScore = 0.0;
        /** 文档切块的目标字符数 */
        private int chunkSize = 500;
        /** 相邻切块的重叠字符数（保留语义连贯性） */
        private int chunkOverlap = 50;
    }

    @Data
    public static class Memory {
        /** 每个 session 保留的最大消息轮数（含 system/user/assistant） */
        private int maxMessages = 20;
    }

    @Data
    public static class Agent {
        /** Agent 的系统提示词（人格 / 行为约束） */
        private String systemPrompt =
                "你是一个有帮助的 AI 助手，回答简洁、准确。当需要实时信息或精确计算时，请调用可用的工具。";
        /** 是否为 Agent 启用知识库检索（RAG） */
        private boolean ragEnabled = true;
        /** 是否启用工具调用（Function Calling） */
        private boolean toolsEnabled = true;
        /** 是否注册内置示例工具（时间 / 计算器） */
        private boolean builtinToolsEnabled = true;
    }
}
