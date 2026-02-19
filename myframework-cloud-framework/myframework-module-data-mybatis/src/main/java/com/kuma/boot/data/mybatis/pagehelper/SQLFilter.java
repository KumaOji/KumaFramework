/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.pagehelper;

import com.kuma.boot.common.utils.lang.StringUtils;

/**
 * SQLFilter
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class SQLFilter {

    public static String sqlInject( String str ) {
        if (StringUtils.isBlank(str)) {
            return null;
        } // 去掉'|"|;|\字符
        str = StringUtils.replace(str, "'", "");
        str = StringUtils.replace(str, "\"", "");
        str = StringUtils.replace(str, ";", "");
        str = StringUtils.replace(str, "\\", ""); // 转换成小写
        str = str.toLowerCase(); // 非法字符
        String[] keywords = {
                "master", "truncate", "insert", "select", "delete", "update", "declare", "alert", "drop"
        }; // 判断是否包含非法字符
        for (String keyword : keywords) {
            if (str.contains(keyword)) {
                throw new RuntimeException("包含非法字符");
            }
        }
        return str;
    }

}
