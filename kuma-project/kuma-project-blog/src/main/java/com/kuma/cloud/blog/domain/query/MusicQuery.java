package com.kuma.cloud.blog.domain.query;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class MusicQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String artist;
    private String album;
    private String genre;
    private Integer status;
    private Integer isRecommend;
    private String orderBy;
    private String orderDirection;
}
