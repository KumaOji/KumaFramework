package com.kuma.cloud.blog.domain.vo;

import lombok.Data;

@Data
public class UaaAuthSettingsVO {

    private boolean captchaEnabled;

    private boolean mfaEnabled;

    private String mfaIssuer;
}
