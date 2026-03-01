/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.xml.bind.annotation.XmlAttribute
 *  jakarta.xml.bind.annotation.XmlRootElement
 */
package com.kuma.boot.dingtalk.xml;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="message")
public class MessageTag {
    private String identityId;
    private String dingerType;
    private BodyTag body;
    private ConfigurationTag configuration;

    @XmlAttribute(required=true, name="id")
    public String getIdentityId() {
        return this.identityId;
    }

    @XmlAttribute(name="type")
    public String getDingerType() {
        return this.dingerType.toUpperCase();
    }

    public BodyTag getBody() {
        return this.body;
    }

    public ConfigurationTag getConfiguration() {
        return this.configuration;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public void setDingerType(String dingerType) {
        this.dingerType = dingerType;
    }

    public void setBody(BodyTag body) {
        this.body = body;
    }

    public void setConfiguration(ConfigurationTag configuration) {
        this.configuration = configuration;
    }
}

