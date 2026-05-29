package com.kuma.cloud.blog.controller;

import com.kuma.boot.ai.model.AiChatRequest;
import com.kuma.boot.ai.model.AiTextRequest;
import com.kuma.boot.ai.model.RagIngestRequest;
import com.kuma.boot.ai.service.AiChatService;
import com.kuma.boot.ai.service.AiEmbeddingService;
import com.kuma.boot.ai.service.AiRagService;
import com.kuma.boot.ai.service.AiTextService;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.cloud.blog.security.BlogPermissions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@Tag(name = "AI 对话")
@RestController
@RequestMapping("/ai-chat")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatService aiChatService;
    private final AiRagService aiRagService;
    private final AiTextService aiTextService;
    private final AiEmbeddingService aiEmbeddingService;

    // ── 基础对话 ─────────────────────────────────────────────────────────────

    @Operation(summary = "非流式对话")
    @PostMapping("/chat")
    @Authorize(BlogPermissions.AI_CHAT_SEND)
    public Result<Map<String, Object>> chat(@RequestBody AiChatRequest request) {
        return Result.success(aiChatService.chat(request));
    }

    @Operation(summary = "流式对话（SSE）")
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Authorize(BlogPermissions.AI_CHAT_SEND)
    public SseEmitter streamChat(@RequestBody AiChatRequest request) {
        return aiChatService.streamChat(request);
    }

    // ── RAG 知识库 ───────────────────────────────────────────────────────────

    @Operation(summary = "写入文档到知识库")
    @PostMapping("/rag/ingest")
    @Authorize(BlogPermissions.AI_CHAT_INGEST)
    public Result<Void> ingest(@RequestBody RagIngestRequest request) {
        aiRagService.ingest(request.getText());
        return Result.success();
    }

    @Operation(summary = "RAG 增强对话")
    @PostMapping("/rag/chat")
    @Authorize(BlogPermissions.AI_CHAT_SEND)
    public Result<Map<String, Object>> ragChat(@RequestBody AiChatRequest request) {
        return Result.success(aiRagService.chat(request));
    }

    @Operation(summary = "RAG 增强流式对话（SSE）")
    @PostMapping(value = "/rag/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Authorize(BlogPermissions.AI_CHAT_SEND)
    public SseEmitter ragStreamChat(@RequestBody AiChatRequest request) {
        return aiRagService.streamChat(request);
    }

    // ── 文本工具 ─────────────────────────────────────────────────────────────

    @Operation(summary = "文章摘要（maxWords 默认 200）")
    @PostMapping("/text/summarize")
    @Authorize(BlogPermissions.AI_CHAT_TEXT)
    public Result<String> summarize(@RequestBody AiTextRequest request) {
        int maxWords = request.getMaxWords() != null ? request.getMaxWords() : 200;
        return Result.success(aiTextService.summarize(request.getText(), maxWords));
    }

    @Operation(summary = "文本翻译（language 指定目标语言，如 English、中文）")
    @PostMapping("/text/translate")
    @Authorize(BlogPermissions.AI_CHAT_TEXT)
    public Result<String> translate(@RequestBody AiTextRequest request) {
        return Result.success(aiTextService.translate(request.getText(), request.getLanguage()));
    }

    @Operation(summary = "关键词提取（count 默认 5）")
    @PostMapping("/text/keywords")
    @Authorize(BlogPermissions.AI_CHAT_TEXT)
    public Result<List<String>> extractKeywords(@RequestBody AiTextRequest request) {
        int count = request.getCount() != null ? request.getCount() : 5;
        return Result.success(aiTextService.extractKeywords(request.getText(), count));
    }

    @Operation(summary = "情感分析（返回 positive / negative / neutral）")
    @PostMapping("/text/sentiment")
    @Authorize(BlogPermissions.AI_CHAT_TEXT)
    public Result<String> sentiment(@RequestBody AiTextRequest request) {
        return Result.success(aiTextService.sentiment(request.getText()));
    }

    @Operation(summary = "自由生成（prompt 直接发给 LLM）")
    @PostMapping("/text/generate")
    @Authorize(BlogPermissions.AI_CHAT_TEXT)
    public Result<String> generate(@RequestBody AiTextRequest request) {
        return Result.success(aiTextService.generate(request.getText(), request.getModel()));
    }

    // ── 向量工具 ─────────────────────────────────────────────────────────────

    @Operation(summary = "文本相似度（余弦相似度，返回 -1~1）")
    @PostMapping("/embedding/similarity")
    @Authorize(BlogPermissions.AI_CHAT_TEXT)
    public Result<Double> similarity(@RequestBody SimilarityRequest request) {
        return Result.success(aiEmbeddingService.cosineSimilarity(request.getText1(), request.getText2()));
    }

    // ── 内部请求体 ──────────────────────────────────────────────────────────

    @lombok.Data
    public static class SimilarityRequest {
        private String text1;
        private String text2;
    }
}
