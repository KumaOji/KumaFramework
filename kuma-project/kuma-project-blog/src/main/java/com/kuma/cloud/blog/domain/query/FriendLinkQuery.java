package com.kuma.cloud.blog.domain.query;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class FriendLinkQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String category;
    private Integer status;
}
