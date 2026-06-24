package com.kuma.cloud.blog.controller;

import com.kuma.boot.ai.model.AiAgentRequest;
import com.kuma.boot.ai.model.AiChatRequest;
import com.kuma.boot.ai.model.AiTextRequest;
import com.kuma.boot.ai.model.RagIngestRequest;
import com.kuma.boot.ai.model.RagMatch;
import com.kuma.boot.ai.model.RagRetrieveRequest;
import com.kuma.boot.ai.model.RagSegment;
import com.kuma.boot.ai.model.RagSource;
import com.kuma.boot.ai.model.RagStats;
import com.kuma.boot.ai.service.AiAgentService;
import com.kuma.boot.ai.service.AiChatService;
import com.kuma.boot.ai.service.AiEmbeddingService;
import com.kuma.boot.ai.service.AiRagService;
import com.kuma.boot.ai.service.AiTextService;
import com.kuma.boot.common.model.result.PageResult;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.cloud.blog.domain.vo.RagSessionVO;
import com.kuma.cloud.blog.domain.dto.SelectionAskDTO;
import com.kuma.cloud.blog.domain.dto.SimilarityDTO;
import com.kuma.cloud.blog.security.BlogPermissions;
import com.kuma.cloud.blog.service.RagSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    private static final Logger log = LoggerFactory.getLogger(AiChatController.class);

    private final AiChatService aiChatService;
    private final AiRagService aiRagService;
    private final AiTextService aiTextService;
    private final AiEmbeddingService aiEmbeddingService;
    private final AiAgentService aiAgentService;
    private final RagSessionService ragSessionService;

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
        int count = aiRagService.ingest(request.getText(), request.getSource());
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
                    log.error("Markdown ingest failed for '{}': {}", name, e.getMessage(), e);
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
                    log.error("Document ingest failed for '{}': {}", name, e.getMessage(), e);
                    detail.put(name, -1);
                }
            });
        }).toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        int total = detail.values().stream().filter(v -> v > 0).mapToInt(Integer::intValue).sum();
        return Result.success(Map.of("totalSegments", total, "files", new LinkedHashMap<>(detail)));
    }

    @Operation(summary = "知识库概览统计（来源数 / 段落数 / 维度 / 状态）")
    @GetMapping("/rag/stats")
    @Authorize(BlogPermissions.AI_CHAT_INGEST)
    public Result<RagStats> ragStats() {
        return Result.success(aiRagService.stats());
    }

    @Operation(summary = "列出知识库已上传文档（按来源聚合，含段落数 / 上传时间 / 大小）")
    @GetMapping("/rag/sources")
    @Authorize(BlogPermissions.AI_CHAT_INGEST)
    public Result<List<RagSource>> listSources() {
        return Result.success(aiRagService.listSources());
    }

    @Operation(summary = "分页查看指定来源的段落（下钻预览切分结果），page 从 0 开始")
    @GetMapping("/rag/segments")
    @Authorize(BlogPermissions.AI_CHAT_INGEST)
    public Result<PageResult<RagSegment>> listSegments(
            @RequestParam("source") String source,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size) {
        return Result.success(aiRagService.listSegments(source, page, size));
    }

    @Operation(summary = "检索测试（返回命中段落及相似度，用于调参）")
    @PostMapping("/rag/retrieve")
    @Authorize(BlogPermissions.AI_CHAT_INGEST)
    public Result<List<RagMatch>> ragRetrieve(@RequestBody RagRetrieveRequest request) {
        return Result.success(aiRagService.retrieve(request.getQuery(), request.getTopK(), request.getMinScore()));
    }

    @Operation(summary = "删除知识库中指定来源的全部段落")
    @DeleteMapping("/rag/sources")
    @Authorize(BlogPermissions.AI_CHAT_INGEST)
    public Result<Map<String, Object>> deleteSource(@RequestParam("source") String source) {
        int deleted = aiRagService.deleteSource(source);
        return Result.success(Map.of("source", source, "deletedSegments", deleted));
    }

    @Operation(summary = "清空整个知识库")
    @DeleteMapping("/rag/sources/all")
    @Authorize(BlogPermissions.AI_CHAT_INGEST)
    public Result<Map<String, Object>> clearKnowledgeBase() {
        int deleted = aiRagService.clearAll();
        return Result.success(Map.of("deletedSegments", deleted));
    }

    @Operation(summary = "创建 RAG 会话（支持自定义标题）")
    @PostMapping("/rag/chat/sessions")
    @Authorize(BlogPermissions.AI_CHAT_RAG)
    public Result<RagSessionVO> createRagSession(@RequestBody(required = false) Map<String, String> body) {
        String title = body != null ? body.get("title") : null;
        return Result.success(ragSessionService.create(title));
    }

    @Operation(summary = "列出所有 RAG 会话（DB 记录 + 内存活跃状态）")
    @GetMapping("/rag/chat/sessions")
    @Authorize(BlogPermissions.AI_CHAT_RAG)
    public Result<List<RagSessionVO>> listRagSessions() {
        return Result.success(ragSessionService.listAll());
    }

    @Operation(summary = "重命名 RAG 会话标题")
    @PutMapping("/rag/chat/sessions/{sessionId}/title")
    @Authorize(BlogPermissions.AI_CHAT_RAG)
    public Result<Void> renameRagSession(@PathVariable String sessionId,
                                         @RequestBody Map<String, String> body) {
        ragSessionService.rename(sessionId, body.get("title"));
        return Result.success();
    }

    @Operation(summary = "删除 RAG 会话（DB + 内存）")
    @DeleteMapping("/rag/chat/sessions/{sessionId}")
    @Authorize(BlogPermissions.AI_CHAT_RAG)
    public Result<Void> deleteRagSession(@PathVariable String sessionId) {
        ragSessionService.delete(sessionId);
        return Result.success();
    }

    @Operation(summary = "清除 RAG 会话记忆（保留 DB 记录，仅清除内存中的历史消息）")
    @DeleteMapping("/rag/chat/memory/{sessionId}")
    @Authorize(BlogPermissions.AI_CHAT_RAG)
    public Result<Void> clearRagChatMemory(@PathVariable String sessionId) {
        aiRagService.clearMemory(sessionId);
        return Result.success();
    }

    @Operation(summary = "RAG 增强对话")
    @PostMapping("/rag/chat")
    @Authorize(BlogPermissions.AI_CHAT_RAG)
    public Result<Map<String, Object>> ragChat(@RequestBody AiChatRequest request) {
        if (request.getSessionId() != null && !request.getSessionId().isBlank()) {
            ragSessionService.ensureExists(request.getSessionId());
        }
        return Result.success(aiRagService.chat(request));
    }

    @Operation(summary = "RAG 增强流式对话（SSE）")
    @PostMapping(value = "/rag/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Authorize(BlogPermissions.AI_CHAT_RAG)
    public SseEmitter ragStreamChat(@RequestBody AiChatRequest request) {
        if (request.getSessionId() != null && !request.getSessionId().isBlank()) {
            ragSessionService.ensureExists(request.getSessionId());
        }
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
    public Result<Double> similarity(@Valid @RequestBody SimilarityDTO request) {
        return Result.success(aiEmbeddingService.cosineSimilarity(request.getText1(), request.getText2()));
    }

    // ── 划词提问 ─────────────────────────────────────────────────────────────

    @Operation(summary = "划词提问（非流式）")
    @PostMapping("/selection/ask")
    @Authorize(BlogPermissions.AI_CHAT_SEND)
    public Result<String> selectionAsk(@Valid @RequestBody SelectionAskDTO request) {
        Map<String, Object> result = aiChatService.chat(buildSelectionChatRequest(request));
        return Result.success(extractContent(result));
    }

    @Operation(summary = "划词提问（流式 SSE）")
    @PostMapping(value = "/selection/ask/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Authorize(BlogPermissions.AI_CHAT_SEND)
    public SseEmitter selectionAskStream(@Valid @RequestBody SelectionAskDTO request) {
        return aiChatService.streamChat(buildSelectionChatRequest(request));
    }

    private AiChatRequest buildSelectionChatRequest(SelectionAskDTO req) {
        AiChatRequest.Message msg = new AiChatRequest.Message();
        msg.setRole("user");
        msg.setContent(buildSelectionPrompt(req));

        AiChatRequest chatRequest = new AiChatRequest();
        chatRequest.setMessages(List.of(msg));
        chatRequest.setModel(req.getModel());
        chatRequest.setSessionId(req.getSessionId());
        return chatRequest;
    }

    private String buildSelectionPrompt(SelectionAskDTO req) {
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
}
