package com.kuma.boot.ai.model;

import lombok.Data;

import java.util.List;

@Data
public class AiChatRequest {

    private String model;
    private List<Message> messages;

    /**
     * 会话 ID。设置后服务端自动维护对话历史（MessageWindowChatMemory）。
     * 不设置则无状态——messages 列表即完整上下文。
     * 使用会话模式时，messages 只需包含本轮新增的用户消息。
     */
    private String sessionId;

    @Data
    public static class Message {
        private String role;
        private String content;
    }
}
