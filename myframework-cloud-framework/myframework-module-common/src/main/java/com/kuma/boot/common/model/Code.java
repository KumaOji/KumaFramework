/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 */
package com.kuma.boot.common.model;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.constant.CommonConstants;
import java.util.function.Function;

public class Code {
    private final String code;

    private Code(String code, String systemCode, String category) {
        this.code = code + systemCode + category;
    }

    private Code(String code) {
        this.code = code;
    }

    public static Code raw(String code) {
        if (StrUtil.isBlank((CharSequence)code)) {
            throw new IllegalArgumentException("code\u4e0d\u80fd\u4e3a\u7a7a");
        }
        return new Code(code);
    }

    public static Code from(String code, String systemCode, String category) {
        if (StrUtil.isBlank((CharSequence)category)) {
            throw new IllegalArgumentException("category\u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (StrUtil.isBlank((CharSequence)systemCode)) {
            throw new IllegalArgumentException("systemCode\u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (StrUtil.isBlank((CharSequence)code)) {
            throw new IllegalArgumentException("code\u4e0d\u80fd\u4e3a\u7a7a");
        }
        return new Code(code, systemCode, category);
    }

    public String getCode() {
        return this.code;
    }

    public static Code systemCode(String code) {
        return Code.code(CommonConstants.DEFAULT_SYSTEM_CODE, CommonConstants.DEFAULT_CATEGORY_CODE).apply(code);
    }

    public static Function<String, Code> code(String systemCode, String category) {
        return code -> new Code((String)code, systemCode, category);
    }
}

