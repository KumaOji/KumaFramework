/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

public class ImageTextDeo {
    private String title;
    private String description;
    private String url;
    private String picUrl;

    private ImageTextDeo(String title, String url, String picUrl) {
        this.title = title;
        this.url = url;
        this.picUrl = picUrl;
    }

    private ImageTextDeo(String title, String description, String url, String picUrl) {
        this(title, url, picUrl);
        this.description = description;
    }

    public static ImageTextDeo instance(String title, String url, String picUrl) {
        return new ImageTextDeo(title, url, picUrl);
    }

    public static ImageTextDeo instance(String title, String description, String url, String picUrl) {
        return new ImageTextDeo(title, description, url, picUrl);
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPicUrl() {
        return this.picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}

