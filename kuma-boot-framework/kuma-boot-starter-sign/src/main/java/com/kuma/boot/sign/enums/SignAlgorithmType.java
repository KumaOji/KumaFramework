package com.kuma.boot.sign.enums;

/**
 * 签名算法类型
 */
public enum SignAlgorithmType {

    /** 跟随全局配置（仅用于注解默认值） */
    DEFAULT,

    /** HMAC-SHA256，appSecret 作为密钥（推荐） */
    HMAC_SHA256,

    /** MD5，签名内容尾部拼接 appSecret 后摘要 */
    MD5
}
