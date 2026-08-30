package com.kuma.cloud.uaa.domain.vo;

import lombok.Data;

/**
 * 登录相关公开配置，供业务方（如 Blog）读取全局开关。
 */
@Data
public class AuthSettingsVO {

    private boolean captchaEnabled;

    private boolean mfaEnabled;

    private String mfaIssuer;
}
