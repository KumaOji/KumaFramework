package com.kuma.boot.sign.algorithm;

import com.kuma.boot.sign.enums.SignAlgorithmType;

/**
 * 签名算法策略
 */
public interface SignAlgorithm {

    /** 支持的算法类型 */
    SignAlgorithmType type();

    /**
     * 对规范化内容计算签名
     *
     * @param content   规范化后的签名原文
     * @param appSecret 应用密钥
     * @return 签名值（十六进制小写）
     */
    String sign(String content, String appSecret);
}
