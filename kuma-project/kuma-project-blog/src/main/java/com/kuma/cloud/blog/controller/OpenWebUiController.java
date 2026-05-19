package com.kuma.cloud.blog.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.cloud.blog.domain.vo.OpenWebUiChatRequest;
import com.kuma.cloud.blog.security.BlogPermissions;
import com.kuma.cloud.blog.service.OpenWebUiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@Tag(name = "AI 对话（OpenWebUI）")
@RestController
@RequestMapping("/openwebui")
@RequiredArgsConstructor
public class OpenWebUiController {

    private final OpenWebUiService openWebUiService;

    @Operation(summary = "获取可用模型列表")
    @GetMapping("/models")
    @Authorize(BlogPermissions.OPENWEBUI_READ)
    public Result<Map<String, Object>> listModels() {
        return Result.success(openWebUiService.listModels());
    }

    @Operation(summary = "非流式对话")
    @PostMapping("/chat")
    @Authorize(BlogPermissions.OPENWEBUI_CHAT)
    public Result<Map<String, Object>> chat(@RequestBody OpenWebUiChatRequest request) {
        return Result.success(openWebUiService.chat(request));
    }

    @Operation(summary = "流式对话（SSE）")
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Authorize(BlogPermissions.OPENWEBUI_CHAT)
    public SseEmitter streamChat(@RequestBody OpenWebUiChatRequest request) {
        return openWebUiService.streamChat(request);
    }
}
