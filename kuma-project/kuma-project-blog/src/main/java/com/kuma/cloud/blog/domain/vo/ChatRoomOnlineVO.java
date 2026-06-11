package com.kuma.cloud.blog.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 管理员在线用户总览：单个聊天空间及其当前在线用户。
 */
@Data
public class ChatRoomOnlineVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long roomId;
    private String roomName;
    private int onlineCount;
    private List<ChatOnlineUserVO> users;
}
