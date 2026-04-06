package com.kuma.cloud.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "blog.wechat")
public class WechatProperties {

    /** 微信开放平台 appId */
    private String appId;

    /** 微信开放平台 appSecret */
    private String appSecret;

    /**
     * 微信回调地址，必须在微信开放平台配置白名单
     * 示例：https://yourdomain.com/api/auth/wechat/callback
     */
    private String redirectUri;

    /** 登录成功后跳转的前端地址，示例：https://yourdomain.com */
    private String frontendUrl;
}
