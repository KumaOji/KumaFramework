package com.kuma.cloud.blog.domain.query;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ProjectQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private Integer category;
    private Integer status;
    private Integer isFeatured;
    private String tech;
}
