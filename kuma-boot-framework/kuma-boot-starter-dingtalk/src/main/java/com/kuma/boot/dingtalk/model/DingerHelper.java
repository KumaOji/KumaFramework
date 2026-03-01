/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.model;

import com.kuma.boot.dingtalk.enums.DingerType;

public abstract class DingerHelper {
    protected static final ThreadLocal<DingerConfig> LOCAL_DINGER = new ThreadLocal();

    public static DingerConfig assignDinger(DingerConfig dingerConfig) {
        dingerConfig.check();
        if (dingerConfig.checkEmpty()) {
            return null;
        }
        DingerHelper.setLocalDinger(dingerConfig);
        return dingerConfig;
    }

    public static DingerConfig assignDinger(String tokenId) {
        DingerConfig dingerConfig = DingerConfig.instance(tokenId);
        return DingerHelper.assignDinger(dingerConfig);
    }

    public static DingerConfig assignDinger(DingerType dingerType, String tokenId) {
        DingerConfig dingerConfig = DingerConfig.instance(dingerType, tokenId);
        return DingerHelper.assignDinger(dingerConfig);
    }

    public static DingerConfig assignDinger(String tokenId, boolean asyncExecute) {
        DingerConfig dingerConfig = DingerConfig.instance(tokenId, asyncExecute);
        return DingerHelper.assignDinger(dingerConfig);
    }

    public static DingerConfig assignDinger(DingerType dingerType, String tokenId, boolean asyncExecute) {
        DingerConfig dingerConfig = DingerConfig.instance(dingerType, tokenId, asyncExecute);
        return DingerHelper.assignDinger(dingerConfig);
    }

    public static DingerConfig assignDinger(String tokenId, String secret) {
        DingerConfig dingerConfig = DingerConfig.instance(tokenId, secret);
        return DingerHelper.assignDinger(dingerConfig);
    }

    public static DingerConfig assignDinger(DingerType dingerType, String tokenId, String secret) {
        DingerConfig dingerConfig = DingerConfig.instance(dingerType, tokenId, secret);
        return DingerHelper.assignDinger(dingerConfig);
    }

    public static DingerConfig assignDinger(String tokenId, String decryptKey, boolean asyncExecute) {
        DingerConfig dingerConfig = DingerConfig.instance(tokenId, asyncExecute);
        dingerConfig.setDecryptKey(decryptKey);
        return DingerHelper.assignDinger(dingerConfig);
    }

    public static DingerConfig assignDinger(DingerType dingerType, String tokenId, String decryptKey, boolean asyncExecute) {
        DingerConfig dingerConfig = DingerConfig.instance(dingerType, tokenId);
        dingerConfig.setDecryptKey(decryptKey);
        dingerConfig.setAsyncExecute(asyncExecute);
        return DingerHelper.assignDinger(dingerConfig);
    }

    public static DingerConfig assignDinger(String tokenId, String decryptKey, String secret) {
        DingerConfig dingerConfig = DingerConfig.instance(tokenId, secret, decryptKey);
        return DingerHelper.assignDinger(dingerConfig);
    }

    public static DingerConfig assignDinger(DingerType dingerType, String tokenId, String decryptKey, String secret) {
        DingerConfig dingerConfig = DingerConfig.instance(dingerType, tokenId, secret, decryptKey);
        return DingerHelper.assignDinger(dingerConfig);
    }

    public static DingerConfig assignDinger(DingerType dingerType, String tokenId, String decryptKey, String secret, boolean asyncExecute) {
        DingerConfig dingerConfig = DingerConfig.instance(dingerType, tokenId, decryptKey, secret, asyncExecute);
        return DingerHelper.assignDinger(dingerConfig);
    }

    protected static void setLocalDinger(DingerConfig config) {
        LOCAL_DINGER.set(config);
    }

    protected static DingerConfig getLocalDinger() {
        return LOCAL_DINGER.get();
    }

    public static void clearDinger() {
        LOCAL_DINGER.remove();
    }
}

