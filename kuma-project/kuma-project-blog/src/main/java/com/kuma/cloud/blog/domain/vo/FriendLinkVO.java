package com.kuma.cloud.blog.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FriendLinkVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String url;
    private String avatar;
    private String description;
    private String category;
    private Integer status;
    private Integer sortOrder;
    private Integer viewCount;
    private LocalDateTime createTime;
}
