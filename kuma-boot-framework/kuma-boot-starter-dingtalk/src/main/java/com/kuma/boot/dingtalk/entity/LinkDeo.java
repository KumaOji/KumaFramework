/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

public class LinkDeo {
    private String title;
    private String text;
    private String messageUrl;
    private String picUrl;

    private LinkDeo(String title, String text, String messageUrl, String picUrl) {
        this.title = title;
        this.text = text;
        this.messageUrl = messageUrl;
        this.picUrl = picUrl;
    }

    public static LinkDeo instance(String title, String text, String messageUrl, String picUrl) {
        return new LinkDeo(title, text, messageUrl, picUrl);
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

