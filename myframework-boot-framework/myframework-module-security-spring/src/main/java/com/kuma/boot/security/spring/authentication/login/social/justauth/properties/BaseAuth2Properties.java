/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.properties;

import java.util.List;

public class BaseAuth2Properties {
    private String clientId;
    private String clientSecret;
    private String codingGroupName;
    private String alipayPublicKey;
    private String proxyHost;
    private Integer proxyPort;
    private Boolean unionId = false;
    private String stackOverflowKey;
    private String agentId;
    private String customizeProviderId;
    private Boolean customizeIsForeign = Boolean.FALSE;
    private String deviceId;
    private Integer clientOsType;
    private String packId;
    private List<String> scopes;

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getCodingGroupName() {
        return this.codingGroupName;
    }

    public void setCodingGroupName(String codingGroupName) {
        this.codingGroupName = codingGroupName;
    }

    public String getAlipayPublicKey() {
        return this.alipayPublicKey;
    }

    public void setAlipayPublicKey(String alipayPublicKey) {
        this.alipayPublicKey = alipayPublicKey;
    }

    public String getProxyHost() {
        return this.proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public Integer getProxyPort() {
        return this.proxyPort;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public Boolean getUnionId() {
        return this.unionId;
    }

    public void setUnionId(Boolean unionId) {
        this.unionId = unionId;
    }

    public String getStackOverflowKey() {
        return this.stackOverflowKey;
    }

    public void setStackOverflowKey(String stackOverflowKey) {
        this.stackOverflowKey = stackOverflowKey;
    }

    public String getAgentId() {
        return this.agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getCustomizeProviderId() {
        return this.customizeProviderId;
    }

    public void setCustomizeProviderId(String customizeProviderId) {
        this.customizeProviderId = customizeProviderId;
    }

    public Boolean getCustomizeIsForeign() {
        return this.customizeIsForeign;
    }

    public void setCustomizeIsForeign(Boolean customizeIsForeign) {
        this.customizeIsForeign = customizeIsForeign;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getClientOsType() {
        return this.clientOsType;
    }

    public void setClientOsType(Integer clientOsType) {
        this.clientOsType = clientOsType;
    }

    public String getPackId() {
        return this.packId;
    }

    public void setPackId(String packId) {
        this.packId = packId;
    }

    public List<String> getScopes() {
        return this.scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }
}

