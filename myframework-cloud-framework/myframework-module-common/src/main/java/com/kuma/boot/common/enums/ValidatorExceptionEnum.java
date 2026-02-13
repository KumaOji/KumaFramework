/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums;

import com.kuma.boot.common.enums.base.CommonEnum;

public enum ValidatorExceptionEnum implements CommonEnum
{
    MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION("A1501", "Parameter\u4f20\u53c2\uff0c\u8bf7\u6c42\u53c2\u6570\u7f3a\u5931\u5f02\u5e38\uff0c\u53c2\u6570\u540d\uff1a{}\uff0c\u7c7b\u578b\u4e3a\uff1a{}"),
    HTTP_MESSAGE_CONVERTER_ERROR("A1502", "\u8bf7\u6c42Json\u6570\u636e\u683c\u5f0f\u9519\u8bef\u6216Json\u5b57\u6bb5\u683c\u5f0f\u8f6c\u5316\u95ee\u9898"),
    HTTP_MEDIA_TYPE_NOT_SUPPORT("A1503", "\u8bf7\u6c42\u7684http media type\u4e0d\u5408\u6cd5"),
    HTTP_METHOD_NOT_SUPPORT("A1504", "\u5f53\u524d\u63a5\u53e3\u4e0d\u652f\u6301{}\u65b9\u5f0f\u8bf7\u6c42"),
    NOT_FOUND("A1505", "404\uff1a\u627e\u4e0d\u5230\u8bf7\u6c42\u7684\u8d44\u6e90"),
    VALIDATED_RESULT_ERROR("A1506", "\u53c2\u6570\u6821\u9a8c\u5931\u8d25\uff0c\u8bf7\u68c0\u67e5\u53c2\u6570\u7684\u4f20\u503c\u662f\u5426\u6b63\u786e\uff0c\u5177\u4f53\u4fe1\u606f\uff1a{}"),
    TABLE_UNIQUE_VALIDATE_ERROR("A1507", "\u6570\u636e\u5e93\u5b57\u6bb5\u503c\u552f\u4e00\u6027\u6821\u9a8c\u51fa\u9519\uff0c\u5177\u4f53\u4fe1\u606f\uff1a{}"),
    CAPTCHA_EMPTY("A1508", "\u9a8c\u8bc1\u7801\u53c2\u6570\u4e0d\u80fd\u4e3a\u7a7a"),
    CAPTCHA_ERROR("A1509", "\u9a8c\u8bc1\u7801\u9519\u8bef"),
    UNIQUE_VALIDATE_SQL_ERROR("A1510", "\u6570\u636e\u5e93\u552f\u4e00\u6027\u6821\u9a8c\u9519\u8bef\uff0csql\u6267\u884c\u9519\u8bef\uff0c\u5177\u4f53\u4fe1\u606f\uff1a{}"),
    DRAG_CAPTCHA_ERROR("A1511", "\u62d6\u62fd\u9a8c\u8bc1\u7801\u9519\u8bef");

    private final String errorCode;
    private final String userTip;

    private ValidatorExceptionEnum(String errorCode, String userTip) {
        this.errorCode = errorCode;
        this.userTip = userTip;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getDesc() {
        return this.errorCode;
    }

    @Override
    public int getCode() {
        return 0;
    }

    public String getNameByCode(int code) {
        for (ValidatorExceptionEnum result : ValidatorExceptionEnum.values()) {
            if (result.getCode() != code) continue;
            return result.name().toLowerCase();
        }
        return null;
    }

    public String getUserTip() {
        return this.userTip;
    }
}

