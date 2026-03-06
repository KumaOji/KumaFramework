package com.kuma.cloud.blog.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ArticleQueryVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String title;
    private Long categoryId;
    private Long authorId;
    private Integer status;
    private Integer isTop;
    private Integer isRecommend;
    private String tag;
}
