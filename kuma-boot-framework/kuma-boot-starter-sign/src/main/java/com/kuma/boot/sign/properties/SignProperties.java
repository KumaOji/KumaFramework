package com.kuma.boot.sign.properties;

import com.kuma.boot.sign.enums.SignAlgorithmType;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * API 签名配置
 *
 * <pre>
 * kuma:
 *   boot:
 *     sign:
 *       enabled: true
 *       algorithm: HMAC_SHA256
 *       timestamp-expire-seconds: 300      # 时间戳允许的时钟偏移窗口
 *       nonce-ttl-seconds: 600             # nonce 去重保留时长（建议 >= 时间戳窗口）
 *       header:
 *         app-id: X-Ca-App-Id
 *         timestamp: X-Ca-Timestamp
 *         nonce: X-Ca-Nonce
 *         signature: X-Ca-Signature
 *       apps:                              # 默认基于配置的 appId → appSecret（生产建议改用 DB Provider）
 *         demo-app: a1b2c3d4e5f6
 * </pre>
 */
@RefreshScope
@ConfigurationProperties(prefix = SignProperties.PREFIX)
public class SignProperties {

    public static final String PREFIX = "kuma.boot.sign";

    private boolean enabled = false;

    /** 全局默认签名算法 */
    private SignAlgorithmType algorithm = SignAlgorithmType.HMAC_SHA256;

    /** 时间戳有效期（秒），请求时间与服务端偏差超过该值视为过期 */
    private long timestampExpireSeconds = 300L;

    /** nonce 去重保留时长（秒） */
    private long nonceTtlSeconds = 600L;

    /** 请求头名称配置 */
    private Header header = new Header();

    /** appId → appSecret（默认 Provider 数据源） */
    private Map<String, String> apps = new LinkedHashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public SignAlgorithmType getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(SignAlgorithmType algorithm) {
        this.algorithm = algorithm;
    }

    public long getTimestampExpireSeconds() {
        return timestampExpireSeconds;
    }

    public void setTimestampExpireSeconds(long timestampExpireSeconds) {
        this.timestampExpireSeconds = timestampExpireSeconds;
    }

    public long getNonceTtlSeconds() {
        return nonceTtlSeconds;
    }

    public void setNonceTtlSeconds(long nonceTtlSeconds) {
        this.nonceTtlSeconds = nonceTtlSeconds;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Map<String, String> getApps() {
        return apps;
    }

    public void setApps(Map<String, String> apps) {
        this.apps = apps;
    }

    /** 请求头名称 */
    public static class Header {
        private String appId = "X-Ca-App-Id";
        private String timestamp = "X-Ca-Timestamp";
        private String nonce = "X-Ca-Nonce";
        private String signature = "X-Ca-Signature";

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getNonce() {
            return nonce;
        }

        public void setNonce(String nonce) {
            this.nonce = nonce;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }
    }
}
