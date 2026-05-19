package com.kuma.cloud.blog.service;

import com.kuma.cloud.blog.domain.vo.OpenWebUiChatRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * OpenWebUI AI 服务接口
 */
public interface OpenWebUiService {

    /**
     * 获取可用模型列表
     */
    Map<String, Object> listModels();

    /**
     * 非流式对话，返回完整响应体
     */
    Map<String, Object> chat(OpenWebUiChatRequest request);

    /**
     * 流式对话，通过 SSE 逐块推送
     */
    SseEmitter streamChat(OpenWebUiChatRequest request);
}
