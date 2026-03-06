package com.kuma.cloud.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("music")
public class Music implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("artist")
    private String artist;

    @TableField("album")
    private String album;

    @TableField("file_path")
    private String filePath;

    @TableField("file_url")
    private String fileUrl;

    @TableField("file_size")
    private Long fileSize;

    @TableField("file_type")
    private String fileType;

    /**
     * Duration in seconds
     */
    @TableField("duration")
    private Integer duration;

    @TableField("cover_image")
    private String coverImage;

    @TableField("genre")
    private String genre;

    @TableField("lyrics")
    private String lyrics;

    @TableField("play_count")
    private Integer playCount;

    @TableField("like_count")
    private Integer likeCount;

    @TableField("status")
    private Integer status;

    @TableField("is_recommend")
    private Integer isRecommend;

    @TableField("upload_user_id")
    private Long uploadUserId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
