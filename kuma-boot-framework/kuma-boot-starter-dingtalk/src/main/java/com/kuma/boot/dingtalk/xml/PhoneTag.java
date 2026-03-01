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

@XmlRootElement(name="phone")
public class PhoneTag {
    private String value;

    @XmlAttribute(required=true, name="value")
    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

