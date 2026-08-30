package com.kuma.cloud.blog.domain.vo;

import lombok.Data;

@Data
public class TotpFeatureVO {

    /** UAA 全局 TOTP 开关 */
    private boolean enabled;
}
