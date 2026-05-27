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
 * CREATE TABLE `chat_room` (
 *   `id`          bigint       NOT NULL AUTO_INCREMENT,
 *   `name`        varchar(50)  NOT NULL,
 *   `description` varchar(200) DEFAULT NULL,
 *   `sort_order`  int          NOT NULL DEFAULT 0,
 *   `status`      tinyint      NOT NULL DEFAULT 1 COMMENT '0=disabled 1=active',
 *   `create_time` datetime     NOT NULL,
 *   `update_time` datetime     NOT NULL,
 *   PRIMARY KEY (`id`)
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
 */
@Data
@TableName("chat_room")
public class ChatRoom implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("sort_order")
    private Integer sortOrder;

    /** 0=disabled 1=active */
    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
