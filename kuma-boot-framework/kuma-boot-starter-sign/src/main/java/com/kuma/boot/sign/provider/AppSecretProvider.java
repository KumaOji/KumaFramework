package com.kuma.boot.sign.provider;

import org.jspecify.annotations.Nullable;

/**
 * AppSecret 查询策略
 *
 * <p>默认实现 {@code PropertiesAppSecretProvider} 基于配置；
 * 生产环境可注册自定义 Bean（如从数据库/缓存查询）覆盖默认实现。
 */
public interface AppSecretProvider {

    /**
     * 根据 appId 获取 appSecret
     *
     * @return appSecret；不存在返回 {@code null}
     */
    @Nullable
    String getSecret(String appId);
}
