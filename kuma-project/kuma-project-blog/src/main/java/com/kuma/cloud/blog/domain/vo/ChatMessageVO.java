package com.kuma.cloud.blog.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ChatMessageVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** CHAT / JOIN / LEAVE / SYSTEM */
    private String type;
    private Long roomId;
    private Long userId;
    private String nickname;
    private String avatar;
    private String content;
    private LocalDateTime timestamp;
}
