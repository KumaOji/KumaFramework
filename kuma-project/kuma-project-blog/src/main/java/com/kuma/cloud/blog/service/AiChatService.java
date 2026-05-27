package com.kuma.cloud.blog.service;

import com.kuma.cloud.blog.domain.vo.AiChatRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface AiChatService {

    Map<String, Object> listModels();

    Map<String, Object> chat(AiChatRequest request);

    SseEmitter streamChat(AiChatRequest request);

    Map<String, Object> ragChat(AiChatRequest request);

    SseEmitter ragStreamChat(AiChatRequest request);
}
