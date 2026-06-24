package com.kuma.cloud.blog.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/** 访客聊天资料（join / send / profile 时由前端传入） */
@Data
public class ChatGuestProfileDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String nickname;
    private String avatar;
}
