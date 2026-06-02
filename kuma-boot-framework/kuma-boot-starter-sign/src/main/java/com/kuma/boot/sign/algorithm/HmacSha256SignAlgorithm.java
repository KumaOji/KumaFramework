package com.kuma.boot.sign.algorithm;

import com.kuma.boot.sign.enums.SignAlgorithmType;
import com.kuma.boot.sign.exception.SignatureException;
import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * HMAC-SHA256 签名：以 appSecret 为密钥对签名原文做 HMAC，输出十六进制小写
 */
public class HmacSha256SignAlgorithm implements SignAlgorithm {

    private static final String ALGORITHM = "HmacSHA256";

    @Override
    public SignAlgorithmType type() {
        return SignAlgorithmType.HMAC_SHA256;
    }

    @Override
    public String sign(String content, String appSecret) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), ALGORITHM));
            byte[] bytes = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return toHex(bytes);
        } catch (Exception e) {
            throw new SignatureException("HMAC-SHA256 签名计算失败", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }
}
