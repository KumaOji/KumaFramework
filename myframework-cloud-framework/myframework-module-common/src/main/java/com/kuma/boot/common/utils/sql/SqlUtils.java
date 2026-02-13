/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.sql;

import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.utils.lang.StringUtils;

public class SqlUtils {
    public static final String SQL_REGEX = "select |insert |delete |update |drop |count |exec |chr |mid |master |truncate |char |and |declare ";
    public static final String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";

    public static String escapeOrderBySql(String value) {
        if (StringUtils.isNotEmpty((CharSequence)value) && !SqlUtils.isValidOrderBySql(value)) {
            throw new BaseException("\u53c2\u6570\u4e0d\u7b26\u5408\u89c4\u8303\uff0c\u4e0d\u80fd\u8fdb\u884c\u67e5\u8be2");
        }
        return value;
    }

    public static boolean isValidOrderBySql(String value) {
        return value.matches(SQL_PATTERN);
    }

    public static void filterKeyword(String value) {
        String[] sqlKeywords;
        if (StringUtils.isEmpty(value)) {
            return;
        }
        for (String sqlKeyword : sqlKeywords = StringUtils.split(SQL_REGEX, "\\|")) {
            if (StringUtils.indexOfIgnoreCase((CharSequence)value, (CharSequence)sqlKeyword) <= -1) continue;
            throw new BaseException("\u53c2\u6570\u5b58\u5728SQL\u6ce8\u5165\u98ce\u9669");
        }
    }
}

