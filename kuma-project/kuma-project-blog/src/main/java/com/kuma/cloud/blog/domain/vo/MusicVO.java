package com.kuma.cloud.blog.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class MusicVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String artist;
    private String album;
    private String fileUrl;
    private Long fileSize;
    private String fileType;
    private Integer duration;
    private String coverImage;
    private String genre;
    private String lyrics;
    private Integer playCount;
    private Integer likeCount;
    private Integer status;
    private Integer isRecommend;
    private Long uploadUserId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
