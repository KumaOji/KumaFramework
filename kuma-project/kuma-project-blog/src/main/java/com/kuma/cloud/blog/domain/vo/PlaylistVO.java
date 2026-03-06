package com.kuma.cloud.blog.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PlaylistVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private String coverImage;
    private Long userId;
    private Integer musicCount;
    private Integer playCount;
    private Integer isPublic;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<MusicVO> musicList;
}
