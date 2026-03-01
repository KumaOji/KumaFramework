/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.xml.bind.annotation.XmlAttribute
 *  jakarta.xml.bind.annotation.XmlElement
 *  jakarta.xml.bind.annotation.XmlElements
 *  jakarta.xml.bind.annotation.XmlRootElement
 */
package com.kuma.boot.dingtalk.xml;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name="phones")
public class PhonesTag {
    private boolean atAll = false;
    private List<PhoneTag> phones;

    @XmlElements(value={@XmlElement(name="phone", type=PhoneTag.class)})
    public List<PhoneTag> getPhones() {
        return this.phones;
    }

    @XmlAttribute
    public boolean getAtAll() {
        return this.atAll;
    }

    public void setAtAll(boolean atAll) {
        this.atAll = atAll;
    }

    public void setPhones(List<PhoneTag> phones) {
        this.phones = phones;
    }
}

