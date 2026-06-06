package com.kuma.cloud.blog.controller;

import com.kuma.boot.ai.model.AiAgentRequest;
import com.kuma.boot.ai.model.AiChatRequest;
import com.kuma.boot.ai.model.AiTextRequest;
import com.kuma.boot.ai.model.RagIngestRequest;
import com.kuma.boot.ai.service.AiAgentService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Tag(name = "AI 对话")
@RestController
@RequestMapping("/ai-chat")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatService aiChatService;
    private final AiRagService aiRagService;
    private final AiTextService aiTextService;
    private final AiEmbeddingService aiEmbeddingService;
    private final AiAgentService aiAgentService;

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

    @Operation(summary = "清除会话记忆（sessionId 绑定的历史消息）")
    @DeleteMapping("/chat/memory/{sessionId}")
    @Authorize(BlogPermissions.AI_CHAT_SEND)
    public Result<Void> clearMemory(@PathVariable String sessionId) {
        aiChatService.clearMemory(sessionId);
        aiRagService.clearMemory(sessionId);
        return Result.success();
    }

    // ── 智能体 Agent（记忆 + RAG + 工具调用）──────────────────────────────────

    @Operation(summary = "智能体对话（自动记忆 + 知识库 + 工具调用）")
    @PostMapping("/agent/chat")
    @Authorize(BlogPermissions.AI_CHAT_SEND)
    public Result<Map<String, Object>> agentChat(@RequestBody AiAgentRequest request) {
        return Result.success(aiAgentService.chat(request));
    }

    @Operation(summary = "智能体流式对话（SSE）")
    @PostMapping(value = "/agent/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Authorize(BlogPermissions.AI_CHAT_SEND)
    public SseEmitter agentStreamChat(@RequestBody AiAgentRequest request) {
        return aiAgentService.streamChat(request);
    }

    @Operation(summary = "清除智能体会话记忆")
    @DeleteMapping("/agent/memory/{sessionId}")
    @Authorize(BlogPermissions.AI_CHAT_SEND)
    public Result<Void> clearAgentMemory(@PathVariable String sessionId) {
        aiAgentService.clearMemory(sessionId);
        return Result.success();
    }

    // ── RAG 知识库 ───────────────────────────────────────────────────────────

    @Operation(summary = "写入纯文本到知识库")
    @PostMapping("/rag/ingest")
    @Authorize(BlogPermissions.AI_CHAT_INGEST)
    public Result<Map<String, Object>> ingest(@RequestBody RagIngestRequest request) {
        int count = aiRagService.ingest(request.getText());
        return Result.success(Map.of("segments", count));
    }

    @Operation(summary = "批量上传 Markdown 文件到知识库")
    @PostMapping(value = "/rag/ingest/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Authorize(BlogPermissions.AI_CHAT_INGEST)
    public Result<Map<String, Object>> ingestFiles(@RequestParam("files") List<MultipartFile> files) {
        // Parallel ingest: Tika parse + vectorize are CPU+network bound — overlap them
        Map<String, Integer> detail = new ConcurrentHashMap<>();
        List<CompletableFuture<Void>> futures = files.stream().map(file -> {
            String name = file.getOriginalFilename();
            return CompletableFuture.runAsync(() -> {
                try {
                    String markdown = new String(file.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                    detail.put(name, aiRagService.ingestMarkdown(name, markdown));
                } catch (Exception e) {
                    detail.put(name, -1);
                }
            });
        }).toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        int total = detail.values().stream().filter(v -> v > 0).mapToInt(Integer::intValue).sum();
        return Result.success(Map.of("totalSegments", total, "files", new LinkedHashMap<>(detail)));
    }

    @Operation(summary = "批量上传任意文档到知识库（PDF/Word/PPT/Excel/HTML，经 Tika 解析）")
    @PostMapping(value = "/rag/ingest/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Authorize(BlogPermissions.AI_CHAT_INGEST)
    public Result<Map<String, Object>> ingestDocuments(@RequestParam("files") List<MultipartFile> files) {
        Map<String, Integer> detail = new ConcurrentHashMap<>();
        List<CompletableFuture<Void>> futures = files.stream().map(file -> {
            String name = file.getOriginalFilename();
            return CompletableFuture.runAsync(() -> {
                try (var stream = file.getInputStream()) {
                    detail.put(name, aiRagService.ingestFile(name, stream));
                } catch (Exception e) {
                    detail.put(name, -1);
                }
            });
        }).toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        int total = detail.values().stream().filter(v -> v > 0).mapToInt(Integer::intValue).sum();
        return Result.success(Map.of("totalSegments", total, "files", new LinkedHashMap<>(detail)));
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

    // ── 划词提问 ─────────────────────────────────────────────────────────────

    @Operation(summary = "划词提问（非流式）")
    @PostMapping("/selection/ask")
    @Authorize(BlogPermissions.AI_CHAT_SEND)
    public Result<String> selectionAsk(@RequestBody SelectionAskRequest request) {
        Map<String, Object> result = aiChatService.chat(buildSelectionChatRequest(request));
        return Result.success(extractContent(result));
    }

    @Operation(summary = "划词提问（流式 SSE）")
    @PostMapping(value = "/selection/ask/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Authorize(BlogPermissions.AI_CHAT_SEND)
    public SseEmitter selectionAskStream(@RequestBody SelectionAskRequest request) {
        return aiChatService.streamChat(buildSelectionChatRequest(request));
    }

    private AiChatRequest buildSelectionChatRequest(SelectionAskRequest req) {
        AiChatRequest.Message msg = new AiChatRequest.Message();
        msg.setRole("user");
        msg.setContent(buildSelectionPrompt(req));

        AiChatRequest chatRequest = new AiChatRequest();
        chatRequest.setMessages(List.of(msg));
        chatRequest.setModel(req.getModel());
        chatRequest.setSessionId(req.getSessionId());
        return chatRequest;
    }

    private String buildSelectionPrompt(SelectionAskRequest req) {
        if (req.getSelectedText() == null || req.getSelectedText().isBlank()) {
            return req.getQuestion() != null ? req.getQuestion() : "";
        }
        String question = (req.getQuestion() != null && !req.getQuestion().isBlank())
                ? req.getQuestion() : "请解释这段文字的含义";
        StringBuilder sb = new StringBuilder();

        sb.append("你正在帮助用户阅读博客文章，请针对【用户选中的内容】回答问题。");
        sb.append("前后文仅作为理解背景，不需要对它们作出解释。\n\n");

        if (req.getPagePath() != null && !req.getPagePath().isBlank()) {
            sb.append("【文章位置】").append(req.getPagePath().replace("/", " > ")).append("\n");
        }
        if (req.getPageTitle() != null && !req.getPageTitle().isBlank()) {
            sb.append("【文章标题】").append(req.getPageTitle()).append("\n");
        }
        sb.append("\n");

        if (req.getContextBefore() != null && !req.getContextBefore().isBlank()) {
            sb.append("【前文（仅供参考）】\n").append(req.getContextBefore().strip()).append("\n\n");
        }
        sb.append("【用户选中的内容】\n").append(req.getSelectedText().strip()).append("\n\n");
        if (req.getContextAfter() != null && !req.getContextAfter().isBlank()) {
            sb.append("【后文（仅供参考）】\n").append(req.getContextAfter().strip()).append("\n\n");
        }

        sb.append("【问题】").append(question);
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static String extractContent(Map<String, Object> response) {
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }

    // ── 内部请求体 ──────────────────────────────────────────────────────────

    @lombok.Data
    public static class SelectionAskRequest {
        /** 用户划选的文字 */
        private String selectedText;
        /** 用户的追加提问，留空则默认解释选中内容 */
        private String question;
        /** 页面标题 */
        private String pageTitle;
        /** 面包屑路径，如 "编程语言/Java/构建工具/插件开发" */
        private String pagePath;
        /** 当前页面 URL/路由，用于日志溯源 */
        private String pageUrl;
        /** 文章 ID，可选，用于 RAG 全文检索 */
        private Long articleId;
        /** 选中内容的前文（约 150 字） */
        private String contextBefore;
        /** 选中内容的后文（约 150 字） */
        private String contextAfter;
        /** 模型名称（可选） */
        private String model;
        /** 会话 ID（可选，设置后支持多轮追问） */
        private String sessionId;
    }

    @lombok.Data
    public static class SimilarityRequest {
        private String text1;
        private String text2;
    }
}
