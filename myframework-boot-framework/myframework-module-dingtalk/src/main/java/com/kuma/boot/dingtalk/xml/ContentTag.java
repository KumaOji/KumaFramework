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

@XmlRootElement(name="content")
public class ContentTag {
    private String title;
    private String content;

    @XmlAttribute
    public String getTitle() {
        return this.title;
    }

    @XmlValue
    public String getContent() {
        return DingerUtils.replaceHeadTailLineBreak(this.content);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

