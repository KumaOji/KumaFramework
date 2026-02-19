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

package com.kuma.boot.data.datasource.cj;

import com.mysql.cj.MysqlConnection;
import com.mysql.cj.Query;
import com.mysql.cj.interceptors.QueryInterceptor;
import com.mysql.cj.log.Log;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.ServerSession;
import com.kuma.boot.common.utils.servlet.MdcUtils;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * url: jdbc:mysql://127.0.0.1:3316/log-helper?useUnicode=true&characterEncoding=UTF8&queryInterceptors=com.redick.support.mysql.Mysql8QueryInterceptor&serverTimezone=CST
 */
public class Mysql8QueryInterceptor implements QueryInterceptor {

    @Override
    public QueryInterceptor init(MysqlConnection mysqlConnection, Properties properties, Log log) {
        return null;
    }

    @Override
    public <T extends Resultset> T preProcess(Supplier<String> supplier, Query query) {
        String start = String.valueOf(System.currentTimeMillis());
        MdcUtils.put("sql_exec_time", start);
        // log.info(LogUtil.customizeMarker(LogUtil.kLOG_KEY_TRACE_TAG, "sql_exec_before"),
        // "开始执行sql");
        return null;
    }

    @Override
    public boolean executeTopLevelOnly() {
        return false;
    }

    @Override
    public void destroy() {}

    @Override
    public <T extends Resultset> T postProcess(
            Supplier<String> supplier, Query query, T t, ServerSession serverSession) {
        long start = Long.parseLong(MdcUtils.get("sql_exec_time"));
        long end = System.currentTimeMillis();
        // log.info(LogUtil.customizeMarker(LogUtil.kLOG_KEY_TRACE_TAG, "sql_exec_after")
        //        .and(append(LogUtil.kLOG_KEY_SQL_EXEC_DURATION, end - start)), "结束执行sql");
        return null;
    }
}
