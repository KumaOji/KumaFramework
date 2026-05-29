package com.kuma.boot.ai.autoconfigure;

import com.kuma.boot.ai.autoconfigure.properties.AiChatProperties;
import com.kuma.boot.ai.service.AiChatService;
import com.kuma.boot.ai.service.AiEmbeddingService;
import com.kuma.boot.ai.service.AiRagService;
import com.kuma.boot.ai.service.AiTextService;
import com.kuma.boot.ai.service.impl.*;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
        String baseUrl = normalize(props.getBaseUrl());
        String key = blank(props.getApiKey()) ? "no-key" : props.getApiKey();
        return OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(key)
                .modelName(props.getModel())
                .logRequests(false)
                .logResponses(false)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(StreamingChatModel.class)
    public StreamingChatModel streamingChatModel(AiChatProperties props) {
        String baseUrl = normalize(props.getBaseUrl());
        String key = blank(props.getApiKey()) ? "no-key" : props.getApiKey();
        return OpenAiStreamingChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(key)
                .modelName(props.getModel())
                .logRequests(false)
                .logResponses(false)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(EmbeddingModel.class)
    public EmbeddingModel embeddingModel(AiChatProperties props) {
        AiChatProperties.Embedding emb = props.getEmbedding();
        return OpenAiEmbeddingModel.builder()
                .baseUrl(normalize(emb.getBaseUrl()))
                .apiKey("no-key")
                .modelName(emb.getModel())
                .build();
    }

    // ── RAG 组件 ─────────────────────────────────────────────────────────────

    @Bean
    @ConditionalOnMissingBean
    public RagComponent ragComponent(EmbeddingModel embeddingModel, AiChatProperties props) {
        return new RagComponent(embeddingModel, props);
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
        return new AiChatServiceImpl(chatModel, streamingChatModel, props.getModel(), aiAsyncExecutor);
    }

    @Bean
    @ConditionalOnMissingBean(AiRagService.class)
    public AiRagService aiRagService(ChatModel chatModel, StreamingChatModel streamingChatModel,
                                      AiChatProperties props, RagComponent ragComponent,
                                      @Qualifier("aiAsyncExecutor") Executor aiAsyncExecutor) {
        return new AiRagServiceImpl(chatModel, streamingChatModel, props.getModel(), ragComponent, aiAsyncExecutor);
    }

    @Bean
    @ConditionalOnMissingBean(AiTextService.class)
    public AiTextService aiTextService(ChatModel chatModel, AiChatProperties props) {
        return new AiTextServiceImpl(chatModel, props.getModel());
    }

    @Bean
    @ConditionalOnMissingBean(AiEmbeddingService.class)
    public AiEmbeddingService aiEmbeddingService(EmbeddingModel embeddingModel) {
        return new AiEmbeddingServiceImpl(embeddingModel);
    }

    // ── 工具方法 ─────────────────────────────────────────────────────────────

    private static String normalize(String url) {
        return (url != null && url.endsWith("/")) ? url.substring(0, url.length() - 1) : url;
    }

    private static boolean blank(String s) {
        return s == null || s.isBlank();
    }
}
