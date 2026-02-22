/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.core.jwt.model;

public enum ExceptionCode {
    SUCCESS(0, "\u6210\u529f"),
    SYSTEM_BUSY(-1, "\u7cfb\u7edf\u7e41\u5fd9~\u8bf7\u7a0d\u540e\u518d\u8bd5~"),
    SYSTEM_TIMEOUT(-2, "\u7cfb\u7edf\u7ef4\u62a4\u4e2d~\u8bf7\u7a0d\u540e\u518d\u8bd5~"),
    PARAM_EX(-3, "\u53c2\u6570\u7c7b\u578b\u89e3\u6790\u5f02\u5e38"),
    SQL_EX(-4, "\u8fd0\u884cSQL\u51fa\u73b0\u5f02\u5e38"),
    NULL_POINT_EX(-5, "\u7a7a\u6307\u9488\u5f02\u5e38"),
    ILLEGAL_ARGUMENT_EX(-6, "\u65e0\u6548\u53c2\u6570\u5f02\u5e38"),
    MEDIA_TYPE_EX(-7, "\u8bf7\u6c42\u7c7b\u578b\u5f02\u5e38"),
    LOAD_RESOURCES_ERROR(-8, "\u52a0\u8f7d\u8d44\u6e90\u51fa\u9519"),
    BASE_VALID_PARAM(-9, "\u7edf\u4e00\u9a8c\u8bc1\u53c2\u6570\u5f02\u5e38"),
    OPERATION_EX(-10, "\u64cd\u4f5c\u5f02\u5e38"),
    SERVICE_MAPPER_ERROR(-11, "Mapper\u7c7b\u8f6c\u6362\u5f02\u5e38"),
    CAPTCHA_ERROR(-12, "\u9a8c\u8bc1\u7801\u6821\u9a8c\u5931\u8d25"),
    JSON_PARSE_ERROR(-13, "JSON\u89e3\u6790\u5f02\u5e38"),
    OK(200, "OK"),
    BAD_REQUEST(400, "\u9519\u8bef\u7684\u8bf7\u6c42"),
    UNAUTHORIZED(401, "\u672a\u8ba4\u8bc1\u7684"),
    FORBIDDEN(403, "\u88ab\u7981\u6b62\u7684"),
    NOT_FOUND(404, "\u6ca1\u6709\u627e\u5230\u8d44\u6e90"),
    METHOD_NOT_ALLOWED(405, "\u4e0d\u652f\u6301\u5f53\u524d\u8bf7\u6c42\u7c7b\u578b"),
    TOO_MANY_REQUESTS(429, "\u8bf7\u6c42\u8d85\u8fc7\u6b21\u6570\u9650\u5236"),
    INTERNAL_SERVER_ERROR(500, "\u5185\u90e8\u670d\u52a1\u9519\u8bef"),
    BAD_GATEWAY(502, "\u7f51\u5173\u9519\u8bef"),
    GATEWAY_TIMEOUT(504, "\u7f51\u5173\u8d85\u65f6"),
    REQUIRED_FILE_PARAM_EX(1001, "\u8bf7\u6c42\u4e2d\u5fc5\u987b\u81f3\u5c11\u5305\u542b\u4e00\u4e2a\u6709\u6548\u6587\u4ef6"),
    DATA_SAVE_ERROR(2000, "\u65b0\u589e\u6570\u636e\u5931\u8d25"),
    DATA_UPDATE_ERROR(2001, "\u4fee\u6539\u6570\u636e\u5931\u8d25"),
    TOO_MUCH_DATA_ERROR(2002, "\u6279\u91cf\u65b0\u589e\u6570\u636e\u8fc7\u591a"),
    JWT_BASIC_INVALID(40000, "\u65e0\u6548\u7684\u57fa\u672c\u8eab\u4efd\u9a8c\u8bc1\u4ee4\u724c"),
    JWT_TOKEN_EXPIRED(40001, "\u4f1a\u8bdd\u8d85\u65f6\uff0c\u8bf7\u91cd\u65b0\u767b\u5f55"),
    JWT_SIGNATURE(40002, "\u4e0d\u5408\u6cd5\u7684token\uff0c\u8bf7\u8ba4\u771f\u6bd4\u5bf9 token \u7684\u7b7e\u540d"),
    JWT_ILLEGAL_ARGUMENT(40003, "\u7f3a\u5c11token\u53c2\u6570"),
    JWT_GEN_TOKEN_FAIL(40004, "\u751f\u6210token\u5931\u8d25"),
    JWT_PARSER_TOKEN_FAIL(40005, "\u89e3\u6790\u7528\u6237\u8eab\u4efd\u9519\u8bef\uff0c\u8bf7\u91cd\u65b0\u767b\u5f55\uff01"),
    JWT_USER_INVALID(40006, "\u7528\u6237\u540d\u6216\u5bc6\u7801\u9519\u8bef"),
    JWT_USER_ENABLED(40007, "\u7528\u6237\u5df2\u7ecf\u88ab\u7981\u7528\uff01"),
    JWT_OFFLINE(40008, "\u60a8\u5df2\u5728\u53e6\u4e00\u4e2a\u8bbe\u5907\u767b\u5f55\uff01"),
    JWT_NOT_LOGIN(40009, "\u767b\u5f55\u8d85\u65f6\uff0c\u8bf7\u91cd\u65b0\u767b\u5f55\uff01");

    private final int code;
    private String msg;

    private ExceptionCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ExceptionCode build(String msg, Object ... param) {
        this.msg = String.format(msg, param);
        return this;
    }

    public ExceptionCode param(Object ... param) {
        this.msg = String.format(this.msg, param);
        return this;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

