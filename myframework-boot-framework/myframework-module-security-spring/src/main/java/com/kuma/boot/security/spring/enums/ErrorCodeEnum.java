/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpStatus
 */
package com.kuma.boot.security.spring.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCodeEnum {
    LOGOUT_SUCCESS(0, "\u767b\u51fa\u6210\u529f"),
    NOT_FOUND(404, "not found"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "\u64cd\u4f5c\u672a\u6388\u6743"),
    INVALID_SESSION(HttpStatus.UNAUTHORIZED.value(), "session \u5931\u6548"),
    EXPIRED_SESSION(HttpStatus.UNAUTHORIZED.value(), "session \u8fc7\u671f"),
    CONCURRENT_SESSION(HttpStatus.UNAUTHORIZED.value(), "\u4f60\u7684\u8d26\u53f7\u5728\u5176\u4ed6\u5ba2\u6237\u7aef\u4e0a\u767b\u5f55, \u6b64\u5ba2\u6237\u7aef\u9000\u51fa\u767b\u5f55\u72b6\u6001, \u5982\u975e\u672c\u4eba, \u8bf7\u66f4\u6539\u5bc6\u7801"),
    SESSION_ENHANCE_CHECK(HttpStatus.UNAUTHORIZED.value(), "session \u975e\u6cd5"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "\u529f\u80fd\u8fd8\u5728\u5f00\u53d1\u4e2d"),
    INTERNAL_SERVER_ERROR(HttpStatus.NOT_FOUND.value(), "\u670d\u52a1\u5668\u5f00\u5c0f\u5dee\uff0c\u8bf7\u91cd\u8bd5"),
    USERNAME_USED(900, "\u7528\u6237\u540d\u5df2\u5b58\u5728"),
    USER_NOT_EXIST(901, "\u7528\u6237\u4e0d\u5b58\u5728"),
    USERNAME_NOT_EMPTY(902, "\u7528\u6237\u540d\u4e0d\u80fd\u4e3a\u7a7a"),
    PASSWORD_NOT_EMPTY(903, "\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a"),
    QUERY_USER_INFO_ERROR(904, "\u672a\u80fd\u83b7\u53d6\u5230\u7528\u6237\u4fe1\u606f\uff0c\u8bf7\u91cd\u8bd5"),
    USER_REGISTER_FAILURE(905, "\u7528\u6237\u6ce8\u518c\u5931\u8d25"),
    GET_REQUEST_PARAMETER_FAILURE(906, "\u83b7\u53d6\u6ce8\u518c\u4fe1\u606f\u5931\u8d25"),
    UPDATE_CONNECTION_DATA_FAILURE(940, "\u66f4\u65b0\u7b2c\u4e09\u65b9\u6388\u6743\u767b\u5f55\u7528\u6237\u4fe1\u606f\u5931\u8d25"),
    REFRESH_TOKEN_FAILURE(950, "refresh Token \u5237\u65b0\u5931\u8d25"),
    AUTH2_PROVIDER_NOT_SUPPORT(960, "\u6b64\u670d\u52a1\u5546\u7684\u7b2c\u4e09\u65b9\u6388\u6743\u767b\u5f55\u4e0d\u652f\u6301"),
    UN_BINDING_ERROR(961, "\u89e3\u7ed1\u7b2c\u4e09\u65b9\u8d26\u53f7\u5f02\u5e38"),
    BINDING_ERROR(962, "\u7ed1\u5b9a\u7b2c\u4e09\u65b9\u8d26\u53f7\u5f02\u5e38"),
    BOUND_ERROR(963, "\u6b64\u7b2c\u4e09\u65b9\u8d26\u53f7\u5df2\u7ed1\u5b9a\u5728\u5176\u4ed6\u8d26\u53f7\u4e0a"),
    QUERY_MOBILE_FAILURE_OF_ONE_CLICK_LOGIN(968, "\u4e00\u952e\u767b\u5f55\u83b7\u53d6\u624b\u673a\u53f7\u5931\u8d25"),
    ACCESS_TOKEN_NOT_EMPTY(969, "accessToken \u4e0d\u80fd\u4e3a\u7a7a"),
    USER_REGISTER_OAUTH2_FAILURE(999, "\u672c\u5730\u7528\u6237\u6ce8\u518c\u6210\u529f, \u7b2c\u4e09\u65b9\u4fe1\u606f\u4fdd\u5b58\u5931\u8d25"),
    VALIDATE_CODE_PARAM_ERROR(600, "\u9a8c\u8bc1\u7801\u53c2\u6570\u9519\u8bef"),
    VALIDATE_CODE_NOT_EMPTY(601, "\u9a8c\u8bc1\u7801\u7684\u503c\u4e0d\u80fd\u4e3a\u7a7a"),
    VALIDATE_CODE_EXPIRED(602, "\u9a8c\u8bc1\u7801\u5df2\u5931\u6548, \u8bf7\u5237\u65b0"),
    VALIDATE_CODE_ERROR(603, "\u9a8c\u8bc1\u7801\u9519\u8bef"),
    ILLEGAL_VALIDATE_CODE_TYPE(604, "\u975e\u6cd5\u7684\u9a8c\u8bc1\u7801\u7c7b\u578b"),
    GET_VALIDATE_CODE_FAILURE(605, "\u83b7\u53d6\u9a8c\u8bc1\u7801\u5931\u8d25\uff0c\u8bf7\u91cd\u8bd5"),
    VALIDATE_CODE_FAILURE(606, "\u9a8c\u8bc1\u7801\u6821\u9a8c\u4e0d\u901a\u8fc7\uff0c\u8bf7\u91cd\u8bd5"),
    SMS_CODE_PARAMETER_ERROR(610, "\u77ed\u4fe1\u9a8c\u8bc1\u7801\u53c2\u6570\u9519\u8bef"),
    SMS_CODE_ERROR(611, "\u77ed\u4fe1\u9a8c\u8bc1\u7801\u9519\u8bef"),
    MOBILE_NOT_EMPTY(620, "\u624b\u673a\u53f7\u4e0d\u80fd\u4e3a\u7a7a"),
    MOBILE_PARAMETER_ERROR(621, "\u624b\u673a\u53f7\u53c2\u6570\u9519\u8bef"),
    MOBILE_FORMAT_ERROR(622, "\u624b\u673a\u53f7\u683c\u5f0f\u9519\u8bef\uff0c\u8bf7\u68c0\u67e5\u4f60\u7684\u624b\u673a\u53f7\u7801"),
    IMAGE_CODE_ERROR(630, "\u56fe\u7247\u9a8c\u8bc1\u7801\u9519\u8bef"),
    TRACK_CODE_ERROR(640, "\u8f68\u8ff9\u9a8c\u8bc1\u7801\u9519\u8bef"),
    SLIDER_CODE_ERROR(650, "\u6ed1\u5757\u9a8c\u8bc1\u7801\u9519\u8bef"),
    SELECTION_CODE_ERROR(660, "\u9009\u62e9\u9a8c\u8bc1\u7801\u9519\u8bef"),
    CUSTOMIZE_CODE_ERROR(670, "\u9a8c\u8bc1\u7801\u9519\u8bef"),
    PARAMETER_ERROR(700, "\u53c2\u6570\u9519\u8bef"),
    BUSINESS_ERROR(701, "\u4e1a\u52a1\u5f02\u5e38"),
    ADD_PERMISSION_FAILURE(710, "\u6dfb\u52a0\u6743\u9650\u5931\u8d25"),
    DEL_PERMISSION_FAILURE(720, "\u5220\u9664\u6743\u9650\u5931\u8d25"),
    PERMISSION_DENY(730, "\u60a8\u6ca1\u6709\u8bbf\u95ee\u6743\u9650\u6216\u672a\u767b\u5f55"),
    REDIRECT_URL_PARAMETER_ILLEGAL(800, "\u975e\u6cd5\u7684\u56de\u8c03\u5730\u5740"),
    REDIRECT_URL_PARAMETER_ERROR(801, "\u56de\u8c03\u5730\u5740\u4e0d\u6b63\u786e"),
    TAMPER_WITH_REDIRECT_URL_PARAMETER(802, "\u56de\u8c03\u53c2\u6570\u88ab\u7be1\u6539"),
    ILLEGAL_ACCESS_URL_ERROR(803, "\u975e\u6cd5\u8bbf\u95ee");

    private final Integer code;
    private final String msg;

    private ErrorCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}

