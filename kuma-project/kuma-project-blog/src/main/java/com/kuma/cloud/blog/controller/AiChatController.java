package com.kuma.cloud.blog.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.cloud.blog.domain.vo.AiChatRequest;
import com.kuma.cloud.blog.security.BlogPermissions;
import com.kuma.cloud.blog.service.AiChatService;
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

    @Operation(summary = "获取可用模型列表")
    @GetMapping("/models")
    @Authorize(BlogPermissions.AI_CHAT_READ)
    public Result<Map<String, Object>> listModels() {
        return Result.success(aiChatService.listModels());
    }

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
}
