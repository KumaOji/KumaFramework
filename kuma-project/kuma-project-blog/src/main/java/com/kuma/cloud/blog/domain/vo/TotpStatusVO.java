package com.kuma.cloud.blog.domain.vo;

import lombok.Data;

@Data
public class TotpStatusVO {

    /** UAA 全局 TOTP 开关 */
    private boolean featureEnabled;

    /** UAA 用户已绑定，登录时强制验证动态码 */
    private boolean enabled;

    /** UAA 用户已绑定或存在尚待确认的绑定会话 */
    private boolean secretBound;
}
