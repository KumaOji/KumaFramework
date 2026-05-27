package com.kuma.cloud.blog.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatUserInfo {

    private Long userId;
    private String nickname;
    private String avatar;
    private String sessionId;
}
