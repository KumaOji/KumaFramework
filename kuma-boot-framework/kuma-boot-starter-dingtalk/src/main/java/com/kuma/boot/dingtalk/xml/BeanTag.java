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

@XmlRootElement(name="dinger")
public class BeanTag {
    private String namespace;
    private List<MessageTag> messages;

    @XmlAttribute(required=true)
    public String getNamespace() {
        return this.namespace;
    }

    @XmlElements(value={@XmlElement(name="message", type=MessageTag.class)})
    public List<MessageTag> getMessages() {
        return this.messages;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setMessages(List<MessageTag> messages) {
        this.messages = messages;
    }
}

