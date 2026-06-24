package com.kuma.cloud.blog.domain.query;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ReadyQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String title;
    private Integer status;
    private Integer priority;
}
