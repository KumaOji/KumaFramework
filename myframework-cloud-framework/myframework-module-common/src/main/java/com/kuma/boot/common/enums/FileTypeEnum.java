/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums;

import com.kuma.boot.common.utils.lang.StringUtils;

public enum FileTypeEnum {
    xls(".xls", "excel", "excel"),
    xlsx(".xlsx", "excel", "excel"),
    doc(".doc", "doc", "word"),
    docx(".docx", "doc", "word"),
    ppt(".ppt", "pp", "ppt"),
    pptx(".pptx", "pp", "ppt"),
    gif(".gif", "image", "\u56fe\u7247"),
    jpg(".jpg", "image", "\u56fe\u7247"),
    jpeg(".jpeg", "image", "\u56fe\u7247"),
    png(".png", "image", "\u56fe\u7247"),
    txt(".txt", "text", "\u6587\u672c"),
    avi(".avi", "video", "\u89c6\u9891"),
    mov(".mov", "video", "\u89c6\u9891"),
    rmvb(".rmvb", "video", "\u89c6\u9891"),
    rm(".rm", "video", "\u89c6\u9891"),
    flv(".flv", "video", "\u89c6\u9891"),
    mp4(".mp4", "video", "\u89c6\u9891"),
    zip(".zip", "zip", "\u538b\u7f29\u5305"),
    pdf(".pdf", "pdf", "pdf");

    private String type;
    private String value;
    private String text;

    private FileTypeEnum(String type, String value, String text) {
        this.type = type;
        this.value = value;
        this.text = text;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static FileTypeEnum getByType(String type) {
        if (StringUtils.isEmpty(type)) {
            return null;
        }
        for (FileTypeEnum val : FileTypeEnum.values()) {
            if (!val.getType().equals(type)) continue;
            return val;
        }
        return null;
    }
}

