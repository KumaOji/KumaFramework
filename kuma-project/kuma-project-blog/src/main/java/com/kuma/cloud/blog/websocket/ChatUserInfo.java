package com.kuma.cloud.blog.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserInfo {

    private Long userId;
    private String nickname;
    private String avatar;
    private String sessionId;
}
