package com.kuma.boot.web.support.encryption.api.core;


import com.kuma.boot.common.support.hash.HashBs;
import com.kuma.boot.common.support.secret.core.SecretBs;

/**
 * 加解密上下文接口
 *
 * @author binbin.hou
 * @since 1.0.0
 */
public interface EncryptionContext {

    /**
     * 加密引导类
     *
     * @return 加密
     */
    SecretBs secretBs();

    /**
     * 哈希引导类
     *
     * @return 哈希
     */
    HashBs hashBs();

}
