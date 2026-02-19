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

package com.kuma.boot.data.datasource.utils;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.util.Assert;

/**
 * Jdbcs
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class Jdbcs {

    public static DbType getDbType( String jdbcUrl ) {
        Assert.hasText(jdbcUrl, "Error: The jabcUrl is Null, Cannot read database type");
        String url = jdbcUrl.toLowerCase();
        if (url.contains(":mysql:") || url.contains(":cobar:")) {
            return DbType.MYSQL;
        } else if (url.contains(":oracle:")) {
            return DbType.ORACLE;
        } else {
            LogUtils.warn(
                    "The jabcUrl is "
                            + jdbcUrl
                            + ", Mybatis Plus Cannot Read Database type or The Database's Not Supported!");
            return DbType.OTHER;
        }
    }

    public static enum DbType {
        MYSQL("mysql", "MySqL数据库"),
        ORACLE("oracle", "Oracle11g及以下数据库（高版本推荐使用ORACLE_NEW）"),
        OTHER("other", "其他数据库");

        DbType( String db, String desc ) {
            this.db = db;
            this.desc = desc;
        }

        public String getDb() {
            return db;
        }

        public String getDesc() {
            return desc;
        }

        private final String db;
        private final String desc;
    }
}
