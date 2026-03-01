/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.idempotent.idempotentenhance.core.em;

public enum IdempotentExceptionEventTypeEnum {
    DELETE_RECORD_ERROR("deleteRecordError", "\u5220\u9664\u5e42\u7b49\u8bb0\u5f55\u5931\u8d25"),
    CHANGE_STATUS_TO_SUCCESS_ERROR("changeStatusToSuccessError", "\u4fee\u6539\u5e42\u7b49\u72b6\u6001\u4e3asuccess\u5931\u8d25");

    private final String type;
    private final String desc;

    private IdempotentExceptionEventTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return this.type;
    }

    public String getDesc() {
        return this.desc;
    }
}

