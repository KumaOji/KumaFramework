package com.kuma.boot.web.support.encryption.api.core;

/**
 * 掩码
 *
 * @since 1.2.0
 */
public interface EncryptMaskFactory {

    /**
     * 实现
     * @param type 请求
     * @return 结果
     */
    com.kuma.boot.web.support.encryption.api.core.EncryptMask get(final String type);

}
