package com.kuma.boot.ai.autoconfigure;

import com.kuma.boot.ai.autoconfigure.properties.AiChatProperties;
import com.kuma.boot.ai.service.AiAgentService;
import com.kuma.boot.ai.service.AiChatService;
import com.kuma.boot.ai.service.AiEmbeddingService;
import com.kuma.boot.ai.service.AiRagService;
import com.kuma.boot.ai.service.AiTextService;
import com.kuma.boot.ai.service.impl.*;
import com.kuma.boot.ai.service.internal.TextAssistant;
import com.kuma.boot.ai.service.tool.AiToolProvider;
import com.kuma.boot.ai.service.tool.BuiltinTools;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@AutoConfiguration
@EnableConfigurationProperties(AiChatProperties.class)
public class AiChatAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(AiChatAutoConfiguration.class, StarterNameConstants.AI_STARTER);
    }

    // ── 基础模型 Bean ────────────────────────────────────────────────────────

    @Bean
    @ConditionalOnMissingBean(ChatModel.class)
    public ChatModel chatModel(AiChatProperties props) {
        String baseUrl = chatBaseUrl(props);
        String key = blank(props.getApiKey()) ? "no-key" : props.getApiKey();
        var builder = OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(key)
                .modelName(props.getModel())
                .timeout(props.getTimeout())
                .logRequests(false)
                .logResponses(false);
        if (props.getTemperature() != null) builder.temperature(props.getTemperature());
        if (props.getMaxTokens() != null)    builder.maxCompletionTokens(props.getMaxTokens());
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean(StreamingChatModel.class)
    public StreamingChatModel streamingChatModel(AiChatProperties props) {
        String baseUrl = chatBaseUrl(props);
        String key = blank(props.getApiKey()) ? "no-key" : props.getApiKey();
        var builder = OpenAiStreamingChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(key)
                .modelName(props.getModel())
                .timeout(props.getTimeout())
                .logRequests(false)
                .logResponses(false);
        if (props.getTemperature() != null) builder.temperature(props.getTemperature());
        if (props.getMaxTokens() != null)    builder.maxCompletionTokens(props.getMaxTokens());
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean(EmbeddingModel.class)
    public EmbeddingModel embeddingModel(AiChatProperties props) {
        AiChatProperties.Embedding emb = props.getEmbedding();
        String key = blank(emb.getApiKey()) ? "no-key" : emb.getApiKey();
        return OpenAiEmbeddingModel.builder()
                .baseUrl(normalize(emb.getBaseUrl()))
                .apiKey(key)
                .modelName(emb.getModel())
                .timeout(props.getTimeout())
                // 诊断用：打印实际发往 embedding 服务的请求与原始响应，定位 0 维向量来源
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    // ── RAG 组件 ─────────────────────────────────────────────────────────────

    @Bean
    @ConditionalOnMissingBean
    public RagComponent ragComponent(EmbeddingModel embeddingModel, AiChatProperties props) {
        return new RagComponent(embeddingModel, props);
    }

    // ── 声明式 AiServices / 工具 ────────────────────────────────────────────────

    /** 文本处理声明式接口，启用结构化输出 */
    @Bean
    @ConditionalOnMissingBean
    public TextAssistant textAssistant(ChatModel chatModel) {
        return AiServices.builder(TextAssistant.class)
                .chatModel(chatModel)
                .build();
    }

    /** 内置示例工具（时间 / 计算器），可通过配置关闭 */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "kuma.boot.ai.agent", name = "builtin-tools-enabled",
            havingValue = "true", matchIfMissing = true)
    public BuiltinTools builtinTools() {
        return new BuiltinTools();
    }

    // ── 异步执行器 ───────────────────────────────────────────────────────────

    @Bean(name = "aiAsyncExecutor")
    @ConditionalOnMissingBean(name = "aiAsyncExecutor")
    public Executor aiAsyncExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    // ── 服务 Bean ────────────────────────────────────────────────────────────

    @Bean
    @ConditionalOnMissingBean(AiChatService.class)
    public AiChatService aiChatService(ChatModel chatModel, StreamingChatModel streamingChatModel,
                                        AiChatProperties props,
                                        @Qualifier("aiAsyncExecutor") Executor aiAsyncExecutor) {
        return new AiChatServiceImpl(chatModel, streamingChatModel,
                props.getModel(), props.getMemory().getMaxMessages(), aiAsyncExecutor);
    }

    @Bean
    @ConditionalOnMissingBean(AiRagService.class)
    public AiRagService aiRagService(ChatModel chatModel, StreamingChatModel streamingChatModel,
                                      AiChatProperties props, RagComponent ragComponent,
                                      @Qualifier("aiAsyncExecutor") Executor aiAsyncExecutor) {
        return new AiRagServiceImpl(chatModel, streamingChatModel,
                props.getModel(), ragComponent, props.getMemory().getMaxMessages(), aiAsyncExecutor);
    }

    @Bean
    @ConditionalOnMissingBean(AiAgentService.class)
    public AiAgentService aiAgentService(ChatModel chatModel, StreamingChatModel streamingChatModel,
                                         AiChatProperties props, RagComponent ragComponent,
                                         ObjectProvider<AiToolProvider> toolProviders) {
        return new AiAgentServiceImpl(chatModel, streamingChatModel, props, ragComponent,
                toolProviders.stream().toList());
    }

    @Bean
    @ConditionalOnMissingBean(AiTextService.class)
    public AiTextService aiTextService(TextAssistant textAssistant, ChatModel chatModel, AiChatProperties props) {
        return new AiTextServiceImpl(textAssistant, chatModel, props.getModel());
    }

    @Bean
    @ConditionalOnMissingBean(AiEmbeddingService.class)
    public AiEmbeddingService aiEmbeddingService(EmbeddingModel embeddingModel) {
        return new AiEmbeddingServiceImpl(embeddingModel);
    }

    // ── 工具方法 ─────────────────────────────────────────────────────────────

    private static String chatBaseUrl(AiChatProperties props) {
        String url = normalize(props.getBaseUrl());
        return blank(url) ? "http://localhost:11434" : url;
    }

    private static String normalize(String url) {
        return (url != null && url.endsWith("/")) ? url.substring(0, url.length() - 1) : url;
    }

    private static boolean blank(String s) {
        return s == null || s.isBlank();
    }
}
