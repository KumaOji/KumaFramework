/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  io.swagger.v3.oas.annotations.media.Schema
 */
package com.kuma.boot.security.spring.core;

import com.google.common.base.MoreObjects;
import io.swagger.v3.oas.annotations.media.Schema;

public class AccessPrincipal {
    @Schema(name="\u540e\u56de\u8c03\u65f6\u5e26\u7684\u53c2\u6570code", title="\u8bbf\u95eeAuthorizeUrl\u540e\u56de\u8c03\u65f6\u5e26\u7684\u53c2\u6570code")
    private String code;
    @Schema(name="\u5c0f\u7a0b\u5e8fappId", title="\u5c0f\u7a0b\u5e8fappId")
    private String appId;
    @Schema(name="\u6d88\u606f\u5bc6\u6587", title="\u5fae\u4fe1\u5c0f\u7a0b\u5e8f\u6d88\u606f\u5bc6\u6587")
    private String encryptedData;
    @Schema(name="\u52a0\u5bc6\u7b97\u6cd5\u7684\u521d\u59cb\u5411\u91cf", title="\u5fae\u4fe1\u5c0f\u7a0b\u5e8f\u52a0\u5bc6\u7b97\u6cd5\u7684\u521d\u59cb\u5411\u91cf")
    private String iv;
    @Schema(name="\u5c0f\u7a0b\u5e8f\u7528\u6237openId", title="\u5c0f\u7a0b\u5e8f\u7528\u6237openId")
    private String openId;
    @Schema(name="\u4f1a\u8bdd\u5bc6\u94a5", title="\u5fae\u4fe1\u5c0f\u7a0b\u5e8f\u4f1a\u8bdd\u5bc6\u94a5")
    private String sessionKey;
    @Schema(name="\u552f\u4e00ID", title="\u5fae\u4fe1\u552f\u4e00ID")
    private String unionId;
    @Schema(name="\u7528\u6237\u975e\u654f\u611f\u4fe1\u606f", title="\u5fae\u4fe1\u5c0f\u7a0b\u5e8f\u7528\u6237\u975e\u654f\u611f\u4fe1\u606f")
    private String rawData;
    @Schema(name="\u7b7e\u540d", title="\u5fae\u4fe1\u5c0f\u7a0b\u5e8f\u7b7e\u540d")
    private String signature;
    @Schema(name="\u540e\u56de\u8c03\u65f6\u5e26\u7684\u53c2\u6570auth_code", title="\u8be5\u53c2\u6570\u76ee\u524d\u53ea\u4f7f\u7528\u4e8e\u652f\u4ed8\u5b9d\u767b\u5f55")
    private String auth_code;
    @Schema(name="\u540e\u56de\u8c03\u65f6\u5e26\u7684\u53c2\u6570state", title="\u7528\u4e8e\u548c\u8bf7\u6c42AuthorizeUrl\u524d\u7684state\u6bd4\u8f83\uff0c\u9632\u6b62CSRF\u653b\u51fb")
    private String state;
    @Schema(name="\u534e\u4e3a\u6388\u6743\u767b\u5f55\u63a5\u53d7code\u7684\u53c2\u6570\u540d")
    private String authorization_code;
    @Schema(name="\u56de\u8c03\u540e\u8fd4\u56de\u7684oauth_token", title="Twitter\u56de\u8c03\u540e\u8fd4\u56de\u7684oauth_token")
    private String oauth_token;
    @Schema(name="\u56de\u8c03\u540e\u8fd4\u56de\u7684oauth_verifier", title="Twitter\u56de\u8c03\u540e\u8fd4\u56de\u7684oauth_verifier")
    private String oauth_verifier;
    @Schema(name="\u624b\u673a\u53f7\u7801", title="\u624b\u673a\u77ed\u4fe1\u767b\u5f55\u552f\u4e00\u6807\u8bc6")
    private String mobile;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getEncryptedData() {
        return this.encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    public String getIv() {
        return this.iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getOpenId() {
        return this.openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getSessionKey() {
        return this.sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getUnionId() {
        return this.unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getRawData() {
        return this.rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAuth_code() {
        return this.auth_code;
    }

    public void setAuth_code(String auth_code) {
        this.auth_code = auth_code;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAuthorization_code() {
        return this.authorization_code;
    }

    public void setAuthorization_code(String authorization_code) {
        this.authorization_code = authorization_code;
    }

    public String getOauth_token() {
        return this.oauth_token;
    }

    public void setOauth_token(String oauth_token) {
        this.oauth_token = oauth_token;
    }

    public String getOauth_verifier() {
        return this.oauth_verifier;
    }

    public void setOauth_verifier(String oauth_verifier) {
        this.oauth_verifier = oauth_verifier;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("code", (Object)this.code).add("appId", (Object)this.appId).add("encryptedData", (Object)this.encryptedData).add("iv", (Object)this.iv).add("openId", (Object)this.openId).add("sessionKey", (Object)this.sessionKey).add("unionId", (Object)this.unionId).add("rawData", (Object)this.rawData).add("signature", (Object)this.signature).add("auth_code", (Object)this.auth_code).add("state", (Object)this.state).add("authorization_code", (Object)this.authorization_code).add("oauth_token", (Object)this.oauth_token).add("oauth_verifier", (Object)this.oauth_verifier).add("mobile", (Object)this.mobile).toString();
    }
}

