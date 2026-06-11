package com.kuma.cloud.blog.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ChatMessageVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 消息记录 ID（历史消息 / 删除事件携带，实时广播消息可为空） */
    private Long id;
    /** CHAT / JOIN / LEAVE / SYSTEM / CLEAR / DELETE */
    private String type;
    private Long roomId;
    private Long userId;
    private String nickname;
    private String avatar;
    private String content;
    private LocalDateTime timestamp;
}
