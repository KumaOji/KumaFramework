package com.kuma.cloud.blog.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RagSessionVO {

    private String id;
    private String title;
    /** true = 内存中有对话历史（本次启动后使用过） */
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
