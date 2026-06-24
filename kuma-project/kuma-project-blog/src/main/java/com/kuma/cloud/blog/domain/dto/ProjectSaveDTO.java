package com.kuma.cloud.blog.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/** 项目新增/编辑入参 */
@Data
public class ProjectSaveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "项目名称不能为空")
    private String name;

    private String summary;

    private String description;

    private String coverImage;

    /** 技术栈，逗号分隔 */
    private String techStack;

    /** 0=个人 1=工作 2=开源 3=学习 */
    private Integer category;

    /** 0=进行中 1=已完成 2=已暂停 */
    private Integer status;

    private LocalDate startDate;

    private LocalDate endDate;

    private String githubUrl;

    private String demoUrl;

    private String docUrl;

    /** 0=普通 1=重点展示 */
    private Integer isFeatured;

    private Integer sortOrder;
}
