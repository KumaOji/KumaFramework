/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

import com.kuma.boot.dingtalk.annatations.DingerLink;
import com.kuma.boot.dingtalk.enums.DingTalkMsgType;
import java.io.Serializable;
import java.util.Map;

public class DingLink
extends DingTalkMessage {
    private Link link;

    public DingLink() {
        this.setMsgtype(DingTalkMsgType.LINK.type());
    }

    public DingLink(Link link) {
        this();
        this.link = link;
    }

    public Link getLink() {
        return this.link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    @Override
    public void transfer(Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (!DingerLink.clazz.isInstance(value)) continue;
            LinkDeo link = (LinkDeo)value;
            this.link = new Link(link.getTitle(), link.getText(), link.getMessageUrl(), link.getPicUrl());
            break;
        }
    }

    public static class Link
    implements Serializable {
        private String title;
        private String text;
        private String messageUrl;
        private String picUrl;

        public Link() {
        }

        public Link(String title, String text, String messageUrl, String picUrl) {
            this.title = title;
            this.text = text;
            this.messageUrl = messageUrl;
            this.picUrl = picUrl;
        }

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return this.text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getMessageUrl() {
            return this.messageUrl;
        }

        public void setMessageUrl(String messageUrl) {
            this.messageUrl = messageUrl;
        }

        public String getPicUrl() {
            return this.picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }
    }
}

