/*
 * Copyright 2023-2024 the original author or authors.
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

package cn.kuma.blog.common.util;

/**
 * SQL 工具类
 * 用于 SQL 字符串转义，防止 SQL 注入
 *
 * @author Kuma
 */
public final class SqlUtils {

    private SqlUtils() {
    }

    /**
     * 转义 SQL 字符串中的特殊字符
     * 将单引号 ' 转义为 ''
     * 注意：此方法仅用于简单的字符串转义，不能完全防止 SQL 注入
     * 建议使用 PreparedStatement 进行参数化查询
     *
     * @param str 需要转义的字符串
     * @return 转义后的字符串
     */
    public static String escapeSql(String str) {
        if (str == null) {
            return null;
        }
        // 将单引号 ' 转义为 ''
        return str.replace("'", "''");
    }

}
