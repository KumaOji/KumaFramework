package com.kuma.cloud.blog.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class LoginVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private String nickname;
    private String email;
    private boolean isAdmin;

    /** 密码验证通过但需要继续输入 TOTP 动态码时为 true，前端据此展示验证码输入框 */
    private boolean requireTotp;
}
