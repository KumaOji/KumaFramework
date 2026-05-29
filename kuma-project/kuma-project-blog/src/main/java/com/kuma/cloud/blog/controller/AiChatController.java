package com.kuma.cloud.blog.controller;

import com.kuma.boot.ai.model.AiChatRequest;
import com.kuma.boot.ai.model.RagIngestRequest;
import com.kuma.boot.ai.service.AiChatService;
import com.kuma.boot.ai.service.impl.RagComponent;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.cloud.blog.security.BlogPermissions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@Tag(name = "AI 对话")
@RestController
@RequestMapping("/ai-chat")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatService aiChatService;
    private final RagComponent ragComponent;

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

    @Operation(summary = "写入文档到知识库")
    @PostMapping("/rag/ingest")
    @Authorize(BlogPermissions.AI_CHAT_INGEST)
    public Result<Void> ingest(@RequestBody RagIngestRequest request) {
        ragComponent.ingest(request.getText());
        return Result.success();
    }

    @Operation(summary = "RAG 对话")
    @PostMapping("/rag/chat")
    @Authorize(BlogPermissions.AI_CHAT_SEND)
    public Result<Map<String, Object>> ragChat(@RequestBody AiChatRequest request) {
        return Result.success(aiChatService.ragChat(request));
    }

    @Operation(summary = "RAG 流式对话（SSE）")
    @PostMapping(value = "/rag/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Authorize(BlogPermissions.AI_CHAT_SEND)
    public SseEmitter ragStreamChat(@RequestBody AiChatRequest request) {
        return aiChatService.ragStreamChat(request);
    }
}
