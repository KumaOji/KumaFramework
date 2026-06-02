package com.kuma.boot.sign.provider;

import com.kuma.boot.sign.properties.SignProperties;
import org.jspecify.annotations.Nullable;

/**
 * 基于配置 {@code kuma.boot.sign.apps} 的 AppSecret 实现
 */
public class PropertiesAppSecretProvider implements AppSecretProvider {

    private final SignProperties properties;

    public PropertiesAppSecretProvider(SignProperties properties) {
        this.properties = properties;
    }

    @Override
    @Nullable
    public String getSecret(String appId) {
        if (appId == null || properties.getApps() == null) {
            return null;
        }
        return properties.getApps().get(appId);
    }
}
