package com.kuma.cloud.blog.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProjectVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String summary;
    private String description;
    private String coverImage;
    private String techStack;
    private Integer category;
    /** 0=个人项目 1=工作项目 2=开源项目 3=学习项目 */
    private String categoryName;
    private Integer status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String githubUrl;
    private String demoUrl;
    private String docUrl;
    private Integer isFeatured;
    private Integer sortOrder;
    private Integer viewCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
