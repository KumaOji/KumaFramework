package com.kuma.boot.ai.model;

import lombok.Data;

import java.util.List;

@Data
public class AiChatRequest {

    private String model;
    private List<Message> messages;
    private Boolean stream;

    @Data
    public static class Message {
        private String role;
        private String content;
    }
}
