package com.kuma.cloud.blog.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class MessageQueryVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String nickname;
    private Integer status;
}
