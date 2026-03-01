/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.enums;

import com.kuma.boot.dingtalk.entity.ExceptionPairs;

public enum ExceptionEnum implements ExceptionPairs
{
    SEND_MSG(1000, "\u53d1\u9001\u6d88\u606f\u5f02\u5e38"),
    MSG_TYPE_CHECK(2000, "\u6d88\u606f\u7c7b\u578b\u5f02\u5e38"),
    ASYNC_CALL(3000, "\u5f02\u6b65\u8c03\u7528\u5f02\u5e38"),
    MULTI_DINGER_SCAN_ERROR(4000, "\u914d\u7f6e\u4e86\u591a\u4e2aDingerScan\u6ce8\u89e3"),
    CONFIG_ERROR(4001, "\u914d\u7f6e\u5f02\u5e38"),
    RESOURCE_CONFIG_EXCEPTION(4002, "\u8bfb\u53d6\u8d44\u6e90[%s]\u4fe1\u606f\u5f02\u5e38"),
    PROPERTIES_ERROR(5000, "\u914d\u7f6e\u6587\u4ef6\u5f02\u5e38"),
    DINER_XML_NAMESPACE_INVALID(6000, "xml\u6587\u4ef6namespace=%s\u5bf9\u5e94\u7684\u7c7b\u4e0d\u5b58\u5728"),
    DINER_XML_MSGTYPE_INVALID(6001, "xml id=%s\u6587\u4ef6message type=%s\u65e0\u6548"),
    DINGERDEFINITION_ERROR(6004, "key=%s\u65e0\u5bf9\u5e94\u7684DingerDefinitionGenerator"),
    DINGERDEFINITIONTYPE_ERROR(6005, "%s\u4e2d\u6d88\u606f\u4f53\u5b9a\u4e49\u4e3b\u7c7b\u578b\u671f\u671b=%s, \u5b9e\u9645=%s"),
    DINGER_REPEATED_EXCEPTION(6500, "\u91cd\u590d\u7684DingerId=%s\u5bf9\u8c61"),
    DINGERDEFINITIONTYPE_UNDEFINED_KEY(6501, "\u5f53\u524dkey=%s\u5728DingerDefinitionType\u4e2d\u6ca1\u5b9a\u4e49"),
    IMAGETEXT_METHOD_PARAM_EXCEPTION(6502, "\u65b9\u6cd5%s\u7684\u53c2\u6570\u4e0d\u7b26\u5408\u56fe\u6587\u6d88\u606f\u5b9a\u4e49\u89c4\u8303"),
    METHOD_DEFINITION_EXCEPTION(6503, "\u65b9\u6cd5%s\u5b9a\u4e49\u4e0d\u7b26\u5408\u89c4\u8303"),
    LINK_METHOD_PARAM_EXCEPTION(6504, "\u65b9\u6cd5%s\u7684\u53c2\u6570\u4e0d\u7b26\u5408Link\u6d88\u606f\u5b9a\u4e49\u89c4\u8303"),
    DINGER_UNSUPPORT_MESSAGE_TYPE_EXCEPTION(6505, "Dinger[%s]\u6682\u4e0d\u652f\u6301\u6d88\u606f\u7c7b\u578b[%s]"),
    DINGER_CONFIG_HANDLER_EXCEPTION(7000, "%s\u4e2d\u6307\u5b9a\u7684dingerconfigs[%d]\u6570\u636e\u5f02\u5e38"),
    MULTIDINGER_ALGORITHM_EXCEPTION(7001, "%s\u4e2d\u7b97\u6cd5\u4e3a\u7a7a"),
    MULTIDINGER_ANNOTATTION_EXCEPTION(7002, "%s\u4e2d\u7684MultiDinger.dinger=%s\u5df2\u7ecf\u88ab\u8b66\u7528"),
    ALGORITHM_FIELD_INSTANCE_NOT_EXISTS(7500, "\u7b97\u6cd5[%s]\u4e2d\u5c5e\u6027\u5b57\u6bb5[%s]\u5b9e\u4f8b\u4e0d\u5b58\u5728"),
    ALGORITHM_FIELD_INSTANCE_NOT_MATCH(7501, "\u7b97\u6cd5[%s]\u4e2d\u5c5e\u6027\u5b57\u6bb5[%s]\u5b9e\u4f8b\u4e0d\u5339\u914d"),
    ALGORITHM_FIELD_INJECT_FAILED(7502, "\u7b97\u6cd5[%s]\u4e2d\u5c5e\u6027\u5b57\u6bb5[%s]\u6ce8\u5165\u5931\u8d25"),
    UNKNOWN(9999, "\u672a\u77e5\u5f02\u5e38");

    private int code;
    private String message;

    private ExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer code() {
        return this.code;
    }

    @Override
    public String desc() {
        return this.message;
    }
}

