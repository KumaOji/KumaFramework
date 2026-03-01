/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.xml.bind.annotation.XmlAttribute
 *  jakarta.xml.bind.annotation.XmlElement
 *  jakarta.xml.bind.annotation.XmlRootElement
 */
package com.kuma.boot.dingtalk.xml;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="configuration")
public class ConfigurationTag {
    private Boolean async;
    private TokenId tokenId;
    private boolean asyncExecute;

    @XmlAttribute(name="async")
    public Boolean getAsync() {
        return this.async;
    }

    @XmlElement(name="token-id")
    public TokenId getTokenId() {
        return this.tokenId;
    }

    @XmlElement(name="async-execute", type=Boolean.class)
    public boolean getAsyncExecute() {
        return this.asyncExecute;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    public void setTokenId(TokenId tokenId) {
        this.tokenId = tokenId;
    }

    public void setAsyncExecute(boolean asyncExecute) {
        this.asyncExecute = asyncExecute;
    }
}

