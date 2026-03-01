/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.enums;

public enum MediaTypeEnum {
    TEXT_HTML("text/html"),
    TEXT_PLAIN("text/plain"),
    TEXT_XML("text/xml"),
    IMG_GIF("image/gif"),
    IMG_JPEG("image/jpeg"),
    IMG_PNG("image/png"),
    XHTML("application/xhtml+xml; charset=utf-8"),
    JSON("application/json; charset=utf-8"),
    XML("application/xml; charset=utf-8"),
    PDF("application/pdf"),
    MSWORD("application/msword"),
    OCTET_STREAM("application/octet-stream"),
    X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"),
    FORM_DATA("multipart/form-data");

    private String type;

    private MediaTypeEnum(String type) {
        this.type = type;
    }

    public String type() {
        return this.type;
    }
}

