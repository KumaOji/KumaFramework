package com.kuma.cloud.blog.domain.vo;

import lombok.Data;

@Data
public class TotpStatusResponse {

    /** totp_enabled = 1，登录时强制验证动态码 */
    private boolean enabled;

    /** totp_secret 已写入数据库（setup 已调用，但可能尚未 enable） */
    private boolean secretBound;
}
