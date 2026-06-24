package com.kuma.cloud.blog.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/** 文章新增/编辑入参 */
@Data
public class ArticleSaveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String summary;

    @NotBlank(message = "正文不能为空")
    private String content;

    private String coverImage;

    private Long categoryId;

    private String tags;

    private Long authorId;

    private String authorName;

    /** 0=草稿 1=发布 */
    private Integer status;

    private Integer isTop;

    private Integer isRecommend;
}
