/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.model;

import com.kuma.boot.dingtalk.enums.DingerType;
import com.kuma.boot.dingtalk.utils.ConfigTools;
import com.kuma.boot.dingtalk.utils.DingerUtils;

public class DingerConfig {
    private DingerType dingerType;
    private String tokenId;
    private String decryptKey;
    private String secret;
    private Boolean asyncExecute;

    protected DingerConfig() {
    }

    private DingerConfig(String tokenId) {
        this.tokenId = tokenId;
    }

    private DingerConfig(DingerType dingerType, String tokenId) {
        this(tokenId);
        this.dingerType = dingerType;
    }

    private DingerConfig(String tokenId, String secret) {
        this.tokenId = tokenId;
        this.secret = secret;
    }

    private DingerConfig(String tokenId, String secret, boolean async) {
        this.tokenId = tokenId;
        this.secret = secret;
        this.asyncExecute = async;
    }

    private DingerConfig(DingerType dingerType, String tokenId, String secret, boolean async) {
        this(tokenId, secret, async);
        this.dingerType = dingerType;
    }

    private DingerConfig(DingerType dingerType, String tokenId, String secret) {
        this(tokenId, secret);
        this.dingerType = dingerType;
    }

    private DingerConfig(String tokenId, boolean async) {
        this.tokenId = tokenId;
        this.asyncExecute = async;
    }

    private DingerConfig(DingerType dingerType, String tokenId, boolean async) {
        this(tokenId, async);
        this.dingerType = dingerType;
    }

    private DingerConfig(String tokenId, String secret, String decryptKey) {
        this.tokenId = tokenId;
        this.decryptKey = decryptKey;
        this.secret = secret;
    }

    private DingerConfig(DingerType dingerType, String tokenId, String secret, String decryptKey) {
        this(tokenId, secret, decryptKey);
        this.dingerType = dingerType;
    }

    private DingerConfig(String tokenId, String secret, String decryptKey, boolean asyncExecute) {
        this.tokenId = tokenId;
        this.decryptKey = decryptKey;
        this.secret = secret;
        this.asyncExecute = asyncExecute;
    }

    private DingerConfig(DingerType dingerType, String tokenId, String secret, String decryptKey, boolean asyncExecute) {
        this(tokenId, secret, decryptKey, asyncExecute);
        this.dingerType = dingerType;
    }

    public static DingerConfig instance(String tokenId) {
        return new DingerConfig(tokenId);
    }

    public static DingerConfig instance(DingerType dingerType, String tokenId) {
        return new DingerConfig(dingerType, tokenId);
    }

    public static DingerConfig instance(String tokenId, String secret) {
        return new DingerConfig(tokenId, secret);
    }

    public static DingerConfig instance(DingerType dingerType, String tokenId, String secret) {
        return new DingerConfig(dingerType, tokenId, secret);
    }

    public static DingerConfig instance(String tokenId, boolean asyncExecute) {
        return new DingerConfig(tokenId, asyncExecute);
    }

    public static DingerConfig instance(DingerType dingerType, String tokenId, boolean asyncExecute) {
        return new DingerConfig(dingerType, tokenId, asyncExecute);
    }

    public static DingerConfig instance(String tokenId, String secret, boolean asyncExecute) {
        return new DingerConfig(tokenId, secret, asyncExecute);
    }

    public static DingerConfig instance(DingerType dingerType, String tokenId, String secret, boolean asyncExecute) {
        return new DingerConfig(dingerType, tokenId, secret, asyncExecute);
    }

    public static DingerConfig instance(String tokenId, String secret, String decryptKey) {
        return new DingerConfig(tokenId, secret, decryptKey);
    }

    public static DingerConfig instance(DingerType dingerType, String tokenId, String secret, String decryptKey) {
        return new DingerConfig(dingerType, tokenId, secret, decryptKey);
    }

    public static DingerConfig instance(String tokenId, String secret, String decryptKey, boolean asyncExecute) {
        return new DingerConfig(tokenId, secret, decryptKey, asyncExecute);
    }

    public static DingerConfig instance(DingerType dingerType, String tokenId, String secret, String decryptKey, boolean asyncExecute) {
        return new DingerConfig(dingerType, tokenId, secret, decryptKey, asyncExecute);
    }

    public void check() {
        if (DingerUtils.isEmpty(this.tokenId)) {
            this.tokenId = null;
            this.decryptKey = null;
            this.secret = null;
        }
    }

    public boolean checkEmpty() {
        return DingerUtils.isEmpty(this.tokenId);
    }

    public DingerConfig merge(DingerConfig dingerConfig) {
        if (DingerUtils.isEmpty(this.tokenId) && DingerUtils.isNotEmpty(dingerConfig.tokenId)) {
            this.tokenId = dingerConfig.tokenId;
            this.decryptKey = dingerConfig.decryptKey;
            this.secret = dingerConfig.secret;
        }
        if (this.asyncExecute == null) {
            this.asyncExecute = dingerConfig.asyncExecute;
        }
        this.check();
        return this;
    }

    public String getTokenId() {
        if (DingerUtils.isEmpty(this.decryptKey)) {
            return this.tokenId;
        }
        try {
            return ConfigTools.decrypt(this.decryptKey, this.tokenId);
        }
        catch (Exception ex) {
            return this.tokenId;
        }
    }

    public DingerType getDingerType() {
        return this.dingerType;
    }

    public void setDingerType(DingerType dingerType) {
        this.dingerType = dingerType;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getDecryptKey() {
        return this.decryptKey;
    }

    public void setDecryptKey(String decryptKey) {
        this.decryptKey = decryptKey;
    }

    public String getSecret() {
        return this.secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Boolean getAsyncExecute() {
        return this.asyncExecute == null ? false : this.asyncExecute;
    }

    public void setAsyncExecute(Boolean asyncExecute) {
        this.asyncExecute = asyncExecute;
    }

    public String toString() {
        return "DingerConfig(dingerType=" + String.valueOf((Object)this.dingerType) + ", tokenId=" + this.tokenId + ", decryptKey=" + this.decryptKey + ", secret=" + this.secret + ", asyncExecute=" + this.asyncExecute + ")";
    }
}

