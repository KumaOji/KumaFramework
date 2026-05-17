package com.kuma.boot.common.model;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.constant.CommonConstants;

import java.util.function.Function;

import static com.kuma.boot.common.constant.CommonConstants.DEFAULT_CATEGORY_CODE;
import static com.kuma.boot.common.constant.CommonConstants.DEFAULT_SYSTEM_CODE;

/**
 * Code
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class Code {

    private final String code;

    private Code( String code, String systemCode, String category ) {
        this.code = code + systemCode + category;
    }

    private Code( String code ) {
        this.code = code;
    }

    public static Code raw( String code ) {
        if (StrUtil.isBlank(code)) {
            throw new IllegalArgumentException("code不能为空");
        }
        return new Code(code);
    }

    public static Code from( String code, String systemCode, String category ) {
        if (StrUtil.isBlank(category)) {
            throw new IllegalArgumentException("category不能为空");
        }
        if (StrUtil.isBlank(systemCode)) {
            throw new IllegalArgumentException("systemCode不能为空");
        }
        if (StrUtil.isBlank(code)) {
            throw new IllegalArgumentException("code不能为空");
        }
        return new Code(code, systemCode, category);
    }

    public String getCode() {
        return code;
    }

    public static Code systemCode( String code ) {
        return code(DEFAULT_SYSTEM_CODE, DEFAULT_CATEGORY_CODE).apply(code);
    }

    public static Function<String, Code> code( String systemCode, String category ) {
        return code -> new Code(code, systemCode, category);
    }
}
