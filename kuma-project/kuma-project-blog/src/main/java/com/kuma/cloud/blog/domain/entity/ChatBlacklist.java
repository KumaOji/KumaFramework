package com.kuma.cloud.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("chat_blacklist")
public class ChatBlacklist {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String email;
    private String reason;
    private LocalDateTime createTime;
}
