/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.xml.bind.annotation.XmlRootElement
 */
package com.kuma.boot.dingtalk.xml;

import com.kuma.boot.dingtalk.enums.MessageSubType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="body")
public class BodyTag {
    private String type = MessageSubType.TEXT.name();
    private ContentTag content;
    private PhonesTag phones;

    public String getType() {
        return this.type;
    }

    public ContentTag getContent() {
        return this.content;
    }

    public PhonesTag getPhones() {
        return this.phones;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(ContentTag content) {
        this.content = content;
    }

    public void setPhones(PhonesTag phones) {
        this.phones = phones;
    }
}

