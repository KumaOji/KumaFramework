package com.kuma.cloud.blog.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/** 聊天空间新增/编辑入参 */
@Data
public class ChatRoomSaveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "聊天空间名称不能为空")
    private String name;

    private String description;

    private Integer sortOrder;

    /** 0=停用 1=启用 */
    private Integer status;
}
