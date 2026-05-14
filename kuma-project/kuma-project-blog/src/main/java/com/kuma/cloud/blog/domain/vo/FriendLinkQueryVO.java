package com.kuma.cloud.blog.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class FriendLinkQueryVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String category;
    private Integer status;
}
