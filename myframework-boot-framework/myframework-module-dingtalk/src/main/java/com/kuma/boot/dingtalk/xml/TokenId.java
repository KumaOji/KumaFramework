/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.xml.bind.annotation.XmlAttribute
 *  jakarta.xml.bind.annotation.XmlRootElement
 *  jakarta.xml.bind.annotation.XmlValue
 */
package com.kuma.boot.dingtalk.xml;

import com.kuma.boot.dingtalk.utils.DingerUtils;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;

@XmlRootElement(name="token-id")
public class TokenId {
    private String secret;
    private String decryptKey;
    private String value;

    @XmlAttribute(name="secret")
    public String getSecret() {
        return this.secret != null ? this.secret.trim() : this.secret;
    }

    @XmlAttribute(name="decrypt-key")
    public String getDecryptKey() {
        return this.decryptKey != null ? this.decryptKey.trim() : this.decryptKey;
    }

    @XmlValue
    public String getValue() {
        return DingerUtils.replaceHeadTailLineBreak(this.value);
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setDecryptKey(String decryptKey) {
        this.decryptKey = decryptKey;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

