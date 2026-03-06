package com.kuma.cloud.blog.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CategoryArticleCountVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long categoryId;
    private String categoryName;
    private long count;
}
