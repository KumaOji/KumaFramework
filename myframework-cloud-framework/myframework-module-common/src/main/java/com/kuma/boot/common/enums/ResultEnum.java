/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.enums.base.CommonEnum;
import com.kuma.boot.common.model.Code;

public enum ResultEnum implements CommonEnum
{
    SUCCESS(Code.systemCode("0000"), CommonConstants.REQUEST_SUCCESS),
    FAILED(Code.systemCode("0001"), CommonConstants.REQUEST_FAILED),
    PROCESSING(Code.systemCode("0001"), CommonConstants.REQUEST_FAILED),
    UNKNOWN(Code.systemCode("0001"), CommonConstants.REQUEST_FAILED),
    CHECK_ERROR(Code.systemCode("500"), CommonConstants.REQUEST_ERROR),
    SYSTEM_ERROR(Code.systemCode("500"), CommonConstants.REQUEST_ERROR),
    IO_ERROR(Code.systemCode("500"), CommonConstants.REQUEST_ERROR),
    NETWORK_ERROR(Code.systemCode("500"), CommonConstants.REQUEST_ERROR),
    NOT_FOUND_ERROR(Code.systemCode("599008"), "\u6570\u636e\u4e0d\u5b58\u5728"),
    DATA_NOT_EXIST_ERROR(Code.systemCode("599008"), "\u6570\u636e\u4e0d\u5b58\u5728"),
    LIMIT_ERROR(Code.systemCode("429"), "\u6570\u636e\u4e0d\u5b58\u5728"),
    TIMEOUT_ERROR(Code.systemCode("429"), "\u8bf7\u6c42\u8d85\u65f6"),
    LOGIN_SUCCESS(Code.systemCode("200"), "\u767b\u5f55\u6210\u529f"),
    LOGOUT_SUCCESS(Code.systemCode("200"), "\u9000\u51fa\u6210\u529f"),
    BAD_REQUEST(Code.systemCode("400"), "\u8bf7\u6c42\u53c2\u6570\u9519\u8bef"),
    ILLEGAL_ARGUMENT_ERROR(Code.systemCode("400"), "\u975e\u6cd5\u53c2\u6570"),
    UNAUTHORIZED(Code.systemCode("401"), "\u672a\u6388\u6743\u7981\u6b62\u8bbf\u95ee"),
    FORBIDDEN(Code.systemCode("403"), "\u6743\u9650\u4e0d\u8db3\u7981\u6b62\u8bbf\u95ee"),
    REQUEST_NOT_FOUND(Code.systemCode("404"), "\u8bf7\u6c42\u4e0d\u5b58\u5728"),
    SQL_INJECTION_REQUEST(Code.systemCode("405"), "\u7591\u4f3cSQL\u6ce8\u5165\u8bf7\u6c42"),
    BLACKLIST(Code.systemCode("406"), "\u60a8\u5df2\u88ab\u62c9\u5165\u9ed1\u540d\u5355"),
    INNER_ERROR(Code.systemCode("500"), "\u670d\u52a1\u5668\u5185\u90e8\u9519\u8bef\uff0c\u8bf7\u8054\u7cfb\u7ba1\u7406\u5458"),
    USER_AUTHORITY_ERROR(Code.systemCode("500"), ""),
    HTTP_MESSAGE_NOT_READABLE(Code.systemCode("500"), ""),
    MISSING_SERVLET_REQUEST_PARAMETER(Code.systemCode("500"), ""),
    USERNAME_OR_PASSWORD_ERROR(Code.systemCode("500"), ""),
    METHOD_NOT_SUPPORTED_ERROR(Code.systemCode("500"), ""),
    MEDIA_TYPE_NOT_SUPPORTED_ERROR(Code.systemCode("500"), ""),
    METHOD_ARGUMENTS_TYPE_MISMATCH(Code.systemCode("500"), "");

    private final Code code;
    private final String desc;

    private ResultEnum(Code code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getMessageByCode(int code) {
        for (ResultEnum result : ResultEnum.values()) {
            if (result.getCode() != code) continue;
            return result.getDesc();
        }
        return null;
    }

    public String getNameByCode(int code) {
        for (ResultEnum result : ResultEnum.values()) {
            if (result.getCode() != code) continue;
            return result.name().toLowerCase();
        }
        return null;
    }

    @Override
    public Code code() {
        return this.code;
    }

    @Override
    public int getCode() {
        return this.code.hashCode();
    }

    @Override
    public String codeDesc() {
        return this.code.getCode();
    }

    @Override
    public String getDesc() {
        return this.desc;
    }
}

