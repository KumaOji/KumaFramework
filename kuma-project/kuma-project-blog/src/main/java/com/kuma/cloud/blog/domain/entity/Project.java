package com.kuma.cloud.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("project")
public class Project implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("summary")
    private String summary;

    @TableField("description")
    private String description;

    @TableField("cover_image")
    private String coverImage;

    /** 技术栈，逗号分隔，如 Java,Spring Boot,MySQL */
    @TableField("tech_stack")
    private String techStack;

    /** 0=个人项目 1=工作项目 2=开源项目 3=学习项目 */
    @TableField("category")
    private Integer category;

    /** 0=进行中 1=已完成 2=已暂停 */
    @TableField("status")
    private Integer status;

    @TableField("start_date")
    private LocalDate startDate;

    @TableField("end_date")
    private LocalDate endDate;

    @TableField("github_url")
    private String githubUrl;

    @TableField("demo_url")
    private String demoUrl;

    @TableField("doc_url")
    private String docUrl;

    /** 0=普通 1=重点展示 */
    @TableField("is_featured")
    private Integer isFeatured;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("view_count")
    private Integer viewCount;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
