package com.kuma.boot.sign.core;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.sign.algorithm.SignAlgorithm;
import com.kuma.boot.sign.annotation.ApiSignature;
import com.kuma.boot.sign.enums.SignAlgorithmType;
import com.kuma.boot.sign.exception.SignatureException;
import com.kuma.boot.sign.properties.SignProperties;
import com.kuma.boot.sign.provider.AppSecretProvider;
import com.kuma.boot.sign.store.NonceStore;
import com.kuma.boot.sign.web.BodyCachingRequestWrapper;
import jakarta.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 签名验签核心编排
 *
 * <p>校验顺序：必填项 → 时间戳时效（防重放）→ appSecret 合法性 → 签名一致性（防篡改）→ nonce 唯一性（防重放）。
 *
 * <p>签名原文规范（客户端需保持一致）：
 * <pre>
 *   sortedParams = 业务参数按 key 字典序拼接：k1=v1&amp;k2=v2 （多值取第一个）
 *   bodyHash     = MD5(requestBody) → hex（仅 Content-Type=application/json 且 body 非空时包含）
 *   content      = sortedParams[&amp;][body={bodyHash}&amp;]appId={appId}&amp;nonce={nonce}&amp;timestamp={timestamp}
 *   HMAC_SHA256  : signature = HmacSHA256(content, appSecret) → hex
 *   MD5          : signature = MD5(content + "&amp;key=" + appSecret) → hex
 * </pre>
 */
public class SignatureValidator {

    private final SignProperties properties;
    private final AppSecretProvider appSecretProvider;
    private final NonceStore nonceStore;
    private final Map<SignAlgorithmType, SignAlgorithm> algorithms = new EnumMap<>(SignAlgorithmType.class);

    public SignatureValidator(SignProperties properties,
                              AppSecretProvider appSecretProvider,
                              NonceStore nonceStore,
                              List<SignAlgorithm> algorithmList) {
        this.properties = properties;
        this.appSecretProvider = appSecretProvider;
        this.nonceStore = nonceStore;
        for (SignAlgorithm algorithm : algorithmList) {
            this.algorithms.put(algorithm.type(), algorithm);
        }
    }

    public void validate(HttpServletRequest request, ApiSignature annotation) {
        SignProperties.Header header = properties.getHeader();
        String appId = request.getHeader(header.getAppId());
        String timestamp = request.getHeader(header.getTimestamp());
        String nonce = request.getHeader(header.getNonce());
        String signature = request.getHeader(header.getSignature());

        if (StringUtils.isBlank(appId) || StringUtils.isBlank(timestamp)
                || StringUtils.isBlank(nonce) || StringUtils.isBlank(signature)) {
            throw new SignatureException("缺少签名参数(appId/timestamp/nonce/signature)");
        }

        // 1. 时间戳时效
        long expireSeconds = annotation.timestampExpireSeconds() > 0
                ? annotation.timestampExpireSeconds()
                : properties.getTimestampExpireSeconds();
        long ts;
        try {
            ts = Long.parseLong(timestamp.trim());
        } catch (NumberFormatException e) {
            throw new SignatureException("时间戳格式非法");
        }
        if (Math.abs(System.currentTimeMillis() - ts) > expireSeconds * 1000L) {
            throw new SignatureException("请求时间戳已过期");
        }

        // 2. appSecret
        String appSecret = appSecretProvider.getSecret(appId);
        if (StringUtils.isBlank(appSecret)) {
            throw new SignatureException("非法的 appId");
        }

        // 3. 签名比对
        SignAlgorithmType type = annotation.algorithm() != SignAlgorithmType.DEFAULT
                ? annotation.algorithm()
                : properties.getAlgorithm();
        SignAlgorithm algorithm = algorithms.get(type);
        if (algorithm == null) {
            throw new SignatureException("不支持的签名算法: " + type);
        }
        String content = buildContent(request, appId, nonce, timestamp);
        String expected = algorithm.sign(content, appSecret);
        if (!expected.equalsIgnoreCase(signature.trim())) {
            LogUtils.warn("签名校验失败, appId={}, expected={}, actual={}", new Object[]{appId, expected, signature});
            throw new SignatureException("签名校验失败");
        }

        // 4. nonce 防重放
        boolean acquired = nonceStore.tryAcquire(appId + ":" + nonce, properties.getNonceTtlSeconds());
        if (!acquired) {
            throw new SignatureException("重复请求(nonce 已使用)");
        }
    }

    /** 业务参数按字典序拼接，可选追加 body MD5，最后追加 appId/nonce/timestamp 形成签名原文 */
    private String buildContent(HttpServletRequest request, String appId, String nonce, String timestamp) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        TreeMap<String, String[]> sorted = new TreeMap<>(parameterMap);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String[]> entry : sorted.entrySet()) {
            String[] values = entry.getValue();
            String value = (values != null && values.length > 0) ? values[0] : "";
            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(entry.getKey()).append('=').append(value);
        }
        // JSON body 纳入签名（由 SignBodyCachingFilter 预先缓存）
        if (request instanceof BodyCachingRequestWrapper wrapper) {
            byte[] body = wrapper.getBody();
            if (body != null && body.length > 0) {
                if (sb.length() > 0) {
                    sb.append('&');
                }
                sb.append("body=").append(md5Hex(body));
            }
        }
        if (sb.length() > 0) {
            sb.append('&');
        }
        sb.append("appId=").append(appId)
          .append("&nonce=").append(nonce)
          .append("&timestamp=").append(timestamp);
        return sb.toString();
    }

    private static String md5Hex(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(bytes);
            StringBuilder hex = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                hex.append(Character.forDigit((b >> 4) & 0xF, 16));
                hex.append(Character.forDigit(b & 0xF, 16));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new SignatureException("body MD5 计算失败", e);
        }
    }
}
