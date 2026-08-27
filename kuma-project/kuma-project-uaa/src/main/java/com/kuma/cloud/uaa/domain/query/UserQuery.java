package com.kuma.cloud.uaa.domain.query;

import lombok.Data;

@Data
public class UserQuery {

    /**
     * 登录名 / 昵称 / 邮箱模糊匹配。
     */
    private String keyword;

    private Integer status;

    private long pageNum = 1;

    private long pageSize = 20;
}
