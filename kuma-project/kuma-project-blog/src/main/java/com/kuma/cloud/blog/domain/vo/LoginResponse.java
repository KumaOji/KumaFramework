package com.kuma.cloud.blog.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class LoginResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String token;
    private Long userId;
    private String username;
    private String nickname;
    private boolean isAdmin;
}
