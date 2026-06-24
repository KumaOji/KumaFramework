package com.kuma.cloud.blog.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/** 待办新增/编辑入参 */
@Data
public class ReadySaveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String content;

    /** 0=pending 1=in-progress 2=completed */
    private Integer status;

    /** 0=normal 1=high 2=urgent */
    private Integer priority;
}
