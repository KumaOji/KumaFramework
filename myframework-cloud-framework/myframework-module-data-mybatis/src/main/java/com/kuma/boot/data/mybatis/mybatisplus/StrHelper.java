/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.data.mybatis.mybatisplus;

import com.baomidou.mybatisplus.core.enums.SqlLike;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;
import com.kuma.boot.common.constant.StrPoolConstants;
import java.io.Serializable;
import java.util.stream.Stream;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ObjUtil;

/**
 * 字符串帮助类
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-04 07:44:45
 */
public final class StrHelper {

    private StrHelper() {}

    public static String getOrDef(String val, String def) {
        return DefValueHelper.getOrDef(val, def);
    }

    /**
     * 有 任意 一个 Blank
     *
     * @param css CharSequence
     * @return boolean
     */
    public static boolean isAnyBlank(final CharSequence... css) {
        if (ObjUtil.isEmpty(css)) {
            return true;
        }
        return Stream.of(css).anyMatch(StrUtil::isBlank);
    }

    /**
     * 是否全非 Blank
     *
     * @param css CharSequence
     * @return boolean
     */
    public static boolean isNoneBlank(final CharSequence... css) {
        if (ObjUtil.isEmpty(css)) {
            return false;
        }
        return Stream.of(css).allMatch(StrUtil::isNotBlank);
    }

    /** mybatis plus like查询转换 */
    public static String keywordConvert(String value) {
        if (StrUtil.isBlank(value)) {
            return StrPoolConstants.EMPTY;
        }
        value = value.replaceAll(StrPoolConstants.PERCENT, "\\\\%");
        value = value.replaceAll(StrPoolConstants.UNDERSCORE, "\\\\_");
        return value;
    }

    /**
     * 拼接like条件
     *
     * @param value 值
     * @param sqlType 拼接类型
     * @return 拼接后的值
     */
    public static String like(Object value, SqlLike sqlType) {
        return SqlUtils.concatLike(keywordConvert(String.valueOf(value)), sqlType);
    }

    /**
     * 拼接like 模糊条件
     *
     * @param value 值
     * @return 拼接后的值
     */
    public static String fullLike(String value) {
        return like(value, SqlLike.DEFAULT);
    }

    /** 默认值 */
    public static final class DefValueHelper {

        private DefValueHelper() {}

        public static String getOrDef(String val, String def) {
            return StrUtil.isEmpty(val) ? def : val;
        }

        public static <T extends Serializable> T getOrDef(T val, T def) {
            return val == null ? def : val;
        }
    }
}
