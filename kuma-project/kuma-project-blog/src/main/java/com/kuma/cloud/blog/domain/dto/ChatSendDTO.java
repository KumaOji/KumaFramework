package com.kuma.cloud.blog.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ChatSendDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String content;

    /** 访客昵称（未登录时由前端传入，可选） */
    private String nickname;

    /** 访客头像 URL 或 letter: 前缀（未登录时由前端传入，可选） */
    private String avatar;
}
