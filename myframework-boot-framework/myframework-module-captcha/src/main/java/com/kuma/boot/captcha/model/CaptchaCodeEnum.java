/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.model;

import java.text.MessageFormat;

public enum CaptchaCodeEnum {
    SUCCESS("0000", "\u6210\u529f"),
    ERROR("0001", "\u64cd\u4f5c\u5931\u8d25"),
    EXCEPTION("9999", "\u670d\u52a1\u5668\u5185\u90e8\u5f02\u5e38"),
    BLANK_ERROR("0011", "{0}\u4e0d\u80fd\u4e3a\u7a7a"),
    NULL_ERROR("0011", "{0}\u4e0d\u80fd\u4e3a\u7a7a"),
    NOT_NULL_ERROR("0012", "{0}\u5fc5\u987b\u4e3a\u7a7a"),
    NOT_EXIST_ERROR("0013", "{0}\u6570\u636e\u5e93\u4e2d\u4e0d\u5b58\u5728"),
    EXIST_ERROR("0014", "{0}\u6570\u636e\u5e93\u4e2d\u5df2\u5b58\u5728"),
    PARAM_TYPE_ERROR("0015", "{0}\u7c7b\u578b\u9519\u8bef"),
    PARAM_FORMAT_ERROR("0016", "{0}\u683c\u5f0f\u9519\u8bef"),
    API_CAPTCHA_INVALID("6110", "\u9a8c\u8bc1\u7801\u5df2\u5931\u6548\uff0c\u8bf7\u91cd\u65b0\u83b7\u53d6"),
    API_CAPTCHA_COORDINATE_ERROR("6111", "\u9a8c\u8bc1\u5931\u8d25"),
    API_CAPTCHA_ERROR("6112", "\u83b7\u53d6\u9a8c\u8bc1\u7801\u5931\u8d25,\u8bf7\u8054\u7cfb\u7ba1\u7406\u5458"),
    API_CAPTCHA_BASEMAP_NULL("6113", "\u5e95\u56fe\u672a\u521d\u59cb\u5316\u6210\u529f\uff0c\u8bf7\u68c0\u67e5\u8def\u5f84"),
    API_REQ_LIMIT_GET_ERROR("6201", "get\u63a5\u53e3\u8bf7\u6c42\u6b21\u6570\u8d85\u9650\uff0c\u8bf7\u7a0d\u540e\u518d\u8bd5!"),
    API_REQ_INVALID("6206", "\u65e0\u6548\u8bf7\u6c42\uff0c\u8bf7\u91cd\u65b0\u83b7\u53d6\u9a8c\u8bc1\u7801"),
    API_REQ_LOCK_GET_ERROR("6202", "\u63a5\u53e3\u9a8c\u8bc1\u5931\u8d25\u6570\u8fc7\u591a\uff0c\u8bf7\u7a0d\u540e\u518d\u8bd5"),
    API_REQ_LIMIT_CHECK_ERROR("6204", "check\u63a5\u53e3\u8bf7\u6c42\u6b21\u6570\u8d85\u9650\uff0c\u8bf7\u7a0d\u540e\u518d\u8bd5!"),
    API_REQ_LIMIT_VERIFY_ERROR("6205", "verify\u8bf7\u6c42\u6b21\u6570\u8d85\u9650!");

    private String code;
    private String desc;

    private CaptchaCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getName() {
        return this.name();
    }

    public String parseError(Object ... fieldNames) {
        return MessageFormat.format(this.desc, fieldNames);
    }
}

