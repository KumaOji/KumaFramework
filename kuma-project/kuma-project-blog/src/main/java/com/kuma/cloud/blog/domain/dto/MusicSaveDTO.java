package com.kuma.cloud.blog.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/** 音乐新增/编辑入参 */
@Data
public class MusicSaveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "歌曲名称不能为空")
    private String name;

    private String artist;

    private String album;

    private String filePath;

    private String fileUrl;

    private Long fileSize;

    private String fileType;

    /** 时长（秒） */
    private Integer duration;

    private String coverImage;

    private String genre;

    private String lyrics;

    private Integer status;

    private Integer isRecommend;

    private Long uploadUserId;
}
