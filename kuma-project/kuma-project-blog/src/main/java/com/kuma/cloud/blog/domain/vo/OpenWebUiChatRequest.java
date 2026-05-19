package com.kuma.cloud.blog.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class OpenWebUiChatRequest {

    private String model;
    private List<Message> messages;
    private Boolean stream;

    @Data
    public static class Message {
        private String role;
        private String content;
    }
}
