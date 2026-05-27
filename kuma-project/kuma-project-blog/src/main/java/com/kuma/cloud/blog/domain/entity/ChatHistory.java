package com.kuma.cloud.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DDL:
 * CREATE TABLE `chat_history` (
 *   `id`           bigint       NOT NULL AUTO_INCREMENT,
 *   `room_id`      bigint       NOT NULL,
 *   `user_id`      bigint       DEFAULT NULL,
 *   `nickname`     varchar(50)  NOT NULL,
 *   `avatar`       varchar(255) DEFAULT NULL,
 *   `content`      text         NOT NULL,
 *   `message_type` varchar(20)  NOT NULL DEFAULT 'CHAT',
 *   `create_time`  datetime     NOT NULL,
 *   PRIMARY KEY (`id`),
 *   KEY `idx_room_id_create_time` (`room_id`, `create_time`)
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
@Data
@TableName("chat_history")
public class ChatHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("room_id")
    private Long roomId;

    @TableField("user_id")
    private Long userId;

    @TableField("nickname")
    private String nickname;

    @TableField("avatar")
    private String avatar;

    @TableField("content")
    private String content;

    /** CHAT / JOIN / LEAVE / SYSTEM */
    @TableField("message_type")
    private String messageType;

    @TableField("create_time")
    private LocalDateTime createTime;
}
