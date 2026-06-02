package com.kuma.boot.sms.tencent.properties;

import com.kuma.boot.sms.common.properties.AbstractHandlerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 腾讯云短信配置
 *
 * <p>继承 {@link AbstractHandlerProperties}。腾讯云模板参数为<b>有序数组</b>，
 * 因此需配置 {@code paramsOrders}（短信类型 → 参数名顺序），发送时按该顺序从
 * {@code NoticeData.params} 取值组装 {@code TemplateParamSet}。
 *
 * <p>示例：
 * <pre>
 * kuma:
 *   boot:
 *     sms:
 *       enabled: true
 *       tencent:
 *         secret-id: AKID****
 *         secret-key: ****
 *         sdk-app-id: 1400000000
 *         sign-name: 我的签名
 *         region: ap-guangzhou
 *         weight: 1
 *         templates:
 *           VERIFICATION_CODE: 1234567        # 短信类型 → 腾讯云模板 ID
 *         params-orders:
 *           VERIFICATION_CODE: [code, expire] # 模板参数 {1}{2} 对应的取值顺序
 * </pre>
 */
@RefreshScope
@ConfigurationProperties(prefix = TencentSmsProperties.PREFIX)
public class TencentSmsProperties extends AbstractHandlerProperties {

    public static final String PREFIX = "kuma.boot.sms.tencent";

    /** SecretId */
    private String secretId;

    /** SecretKey */
    private String secretKey;

    /** 短信应用 SdkAppId */
    private String sdkAppId;

    /** 短信签名内容 */
    private String signName;

    /** 地域，默认广州 */
    private String region = "ap-guangzhou";

    /** 国内手机号默认区号前缀 */
    private String defaultRegionCode = "+86";

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSdkAppId() {
        return sdkAppId;
    }

    public void setSdkAppId(String sdkAppId) {
        this.sdkAppId = sdkAppId;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDefaultRegionCode() {
        return defaultRegionCode;
    }

    public void setDefaultRegionCode(String defaultRegionCode) {
        this.defaultRegionCode = defaultRegionCode;
    }
}
