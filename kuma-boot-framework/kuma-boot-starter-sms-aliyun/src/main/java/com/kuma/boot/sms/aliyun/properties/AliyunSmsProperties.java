package com.kuma.boot.sms.aliyun.properties;

import com.kuma.boot.sms.common.properties.AbstractHandlerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 阿里云短信配置
 *
 * <p>继承 {@link AbstractHandlerProperties}，复用其 {@code templates}（短信类型 → 模板 Code）、
 * {@code paramsOrders}、{@code weight}（多通道负载均衡权重）。
 *
 * <p>示例：
 * <pre>
 * kuma:
 *   boot:
 *     sms:
 *       enabled: true
 *       aliyun:
 *         access-key-id: LTAI****
 *         access-key-secret: ****
 *         sign-name: 我的签名
 *         weight: 1
 *         templates:
 *           VERIFICATION_CODE: SMS_123456789   # 短信类型 → 阿里云模板 Code
 * </pre>
 */
@RefreshScope
@ConfigurationProperties(prefix = AliyunSmsProperties.PREFIX)
public class AliyunSmsProperties extends AbstractHandlerProperties {

    public static final String PREFIX = "kuma.boot.sms.aliyun";

    /** AccessKey ID */
    private String accessKeyId;

    /** AccessKey Secret */
    private String accessKeySecret;

    /** 短信签名 */
    private String signName;

    /** 服务接入点，默认公网 endpoint */
    private String endpoint = "dysmsapi.aliyuncs.com";

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
