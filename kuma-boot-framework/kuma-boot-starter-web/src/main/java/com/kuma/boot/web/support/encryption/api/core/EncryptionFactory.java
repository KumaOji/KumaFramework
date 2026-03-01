package com.kuma.boot.web.support.encryption.api.core;


/**
 * 工厂
 *
 * @author binbin.hou
 * @since 1.2.0
 */
public interface EncryptionFactory {

    /**
     * 实现
     * @param type 请求
     * @return 结果
     */
    com.kuma.boot.web.support.encryption.api.core.Encryption get(final String type);

}
