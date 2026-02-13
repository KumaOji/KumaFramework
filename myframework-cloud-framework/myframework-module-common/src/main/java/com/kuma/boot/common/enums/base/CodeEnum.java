/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums.base;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.model.Code;

public interface CodeEnum {
    public int getCode();

    default public Code code() {
        return ResultEnum.FAILED.code();
    }

    default public String codeDesc() {
        return "";
    }
}

