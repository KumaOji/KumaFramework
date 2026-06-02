package com.kuma.boot.sign.algorithm;

import com.kuma.boot.sign.enums.SignAlgorithmType;
import com.kuma.boot.sign.exception.SignatureException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * MD5 签名：在签名原文尾部拼接 {@code &key=appSecret} 后做 MD5，输出十六进制小写
 *
 * <p>安全性弱于 HMAC-SHA256，仅用于兼容存量三方约定。
 */
public class Md5SignAlgorithm implements SignAlgorithm {

    @Override
    public SignAlgorithmType type() {
        return SignAlgorithmType.MD5;
    }

    @Override
    public String sign(String content, String appSecret) {
        try {
            String source = content + "&key=" + appSecret;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(source.getBytes(StandardCharsets.UTF_8));
            return toHex(bytes);
        } catch (Exception e) {
            throw new SignatureException("MD5 签名计算失败", e);
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
