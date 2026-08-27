package com.kuma.cloud.uaa.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CaptchaVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 提交登录表单时需回传，服务端据此在 Redis 中取回答案 */
    private String captchaKey;

    /** data URI 形式的 PNG 图片，可直接作为 img src */
    private String image;

    private long expiresInSeconds;
}
