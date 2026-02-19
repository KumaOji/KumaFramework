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

package com.kuma.boot.data.datasource.druid;

import com.alibaba.druid.pool.DruidDataSourceStatLogger;
import com.alibaba.druid.pool.DruidDataSourceStatLoggerAdapter;
import com.alibaba.druid.pool.DruidDataSourceStatLoggerImpl;
import com.alibaba.druid.pool.DruidDataSourceStatValue;
import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * DruidStatLogger
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class DruidStatLogger extends DruidDataSourceStatLoggerAdapter implements DruidDataSourceStatLogger {

    private static final Log LOG = LogFactory.getLog(DruidDataSourceStatLoggerImpl.class);

    private Log logger = LOG;

    public DruidStatLogger() {
        this.configFromProperties(System.getProperties());
    }

    public boolean isLogEnable() {
        return logger.isInfoEnabled();
    }

    public void log( String value ) {
        logger.info(value);
    }

    @Override
    public void log( DruidDataSourceStatValue druidDataSourceStatValue ) {
        if (!isLogEnable()) {
            return;
        }
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("dbType", druidDataSourceStatValue.getDbType());
        map.put("name", druidDataSourceStatValue.getName());
        map.put("activeCount", druidDataSourceStatValue.getActiveCount());

        if (druidDataSourceStatValue.getActivePeak() > 0) {
            map.put("activePeak", druidDataSourceStatValue.getActivePeak());
            map.put("activePeakTime", druidDataSourceStatValue.getActivePeakTime());
        }
        map.put("poolingCount", druidDataSourceStatValue.getPoolingCount());
        if (druidDataSourceStatValue.getPoolingPeak() > 0) {
            map.put("poolingPeak", druidDataSourceStatValue.getPoolingPeak());
            map.put("poolingPeakTime", druidDataSourceStatValue.getPoolingPeakTime());
        }
        map.put("connectCount", druidDataSourceStatValue.getConnectCount());
        map.put("closeCount", druidDataSourceStatValue.getCloseCount());

        if (druidDataSourceStatValue.getWaitThreadCount() > 0) {
            map.put("waitThreadCount", druidDataSourceStatValue.getWaitThreadCount());
        }

        if (druidDataSourceStatValue.getNotEmptyWaitCount() > 0) {
            map.put("notEmptyWaitCount", druidDataSourceStatValue.getNotEmptyWaitCount());
        }

        if (druidDataSourceStatValue.getNotEmptyWaitMillis() > 0) {
            map.put("notEmptyWaitMillis", druidDataSourceStatValue.getNotEmptyWaitMillis());
        }

        if (druidDataSourceStatValue.getLogicConnectErrorCount() > 0) {
            map.put("logicConnectErrorCount", druidDataSourceStatValue.getLogicConnectErrorCount());
        }

        if (druidDataSourceStatValue.getPhysicalConnectCount() > 0) {
            map.put("physicalConnectCount", druidDataSourceStatValue.getPhysicalConnectCount());
        }

        if (druidDataSourceStatValue.getPhysicalCloseCount() > 0) {
            map.put("physicalCloseCount", druidDataSourceStatValue.getPhysicalCloseCount());
        }

        if (druidDataSourceStatValue.getPhysicalConnectErrorCount() > 0) {
            map.put(
                    "physicalConnectErrorCount",
                    druidDataSourceStatValue.getPhysicalConnectErrorCount());
        }

        if (druidDataSourceStatValue.getExecuteCount() > 0) {
            map.put("executeCount", druidDataSourceStatValue.getExecuteCount());
        }

        if (druidDataSourceStatValue.getErrorCount() > 0) {
            map.put("errorCount", druidDataSourceStatValue.getErrorCount());
        }

        if (druidDataSourceStatValue.getCommitCount() > 0) {
            map.put("commitCount", druidDataSourceStatValue.getCommitCount());
        }

        if (druidDataSourceStatValue.getRollbackCount() > 0) {
            map.put("rollbackCount", druidDataSourceStatValue.getRollbackCount());
        }

        if (druidDataSourceStatValue.getPstmtCacheHitCount() > 0) {
            map.put("pstmtCacheHitCount", druidDataSourceStatValue.getPstmtCacheHitCount());
        }

        if (druidDataSourceStatValue.getPstmtCacheMissCount() > 0) {
            map.put("pstmtCacheMissCount", druidDataSourceStatValue.getPstmtCacheMissCount());
        }

        if (druidDataSourceStatValue.getStartTransactionCount() > 0) {
            map.put("startTransactionCount", druidDataSourceStatValue.getStartTransactionCount());
            map.put("transactionHistogram", ( druidDataSourceStatValue.getTransactionHistogram() ));
        }

        if (druidDataSourceStatValue.getConnectCount() > 0) {
            map.put(
                    "connectionHoldTimeHistogram",
                    ( druidDataSourceStatValue.getConnectionHoldTimeHistogram() ));
        }

        if (druidDataSourceStatValue.getClobOpenCount() > 0) {
            map.put("clobOpenCount", druidDataSourceStatValue.getClobOpenCount());
        }

        if (druidDataSourceStatValue.getBlobOpenCount() > 0) {
            map.put("blobOpenCount", druidDataSourceStatValue.getBlobOpenCount());
        }

        if (druidDataSourceStatValue.getSqlSkipCount() > 0) {
            map.put("sqlSkipCount", druidDataSourceStatValue.getSqlSkipCount());
        }
        if (!isLogEnable()) {
            return;
        }
        // Map<String, Object> map = new LinkedHashMap<String, Object>();
        myArrayList<Map<String, Object>> sqlList = new myArrayList<Map<String, Object>>();

        // 有执行sql的话 只显示sql语句
        if (druidDataSourceStatValue.getSqlList().size() > 0) {
            for (JdbcSqlStatValue sqlStat : druidDataSourceStatValue.getSqlList()) {
                Map<String, Object> sqlStatMap = new LinkedHashMap<String, Object>();
                sqlStatMap.put("执行了sql语句： ", sqlStat.getSql());
                sqlList.add(sqlStatMap);
                String text = sqlList.toString();
                // log(text);
            }
        }
        // 没有sql语句的话就显示最上面那些
        else {
            String text = map.toString();
            log(text);
        }
    }

    @Override
    public void configFromProperties( Properties properties ) {
        String property = properties.getProperty("druid.stat.loggerName");
        if (property != null && property.length() > 0) {
            setLoggerName(property);
        }
    }

    @Override
    public void setLogger( Log log ) {
        if (log == null) {
            throw new IllegalArgumentException("logger can not be null");
        }
        this.logger = log;
    }

    @Override
    public void setLoggerName( String loggerName ) {
        logger = LogFactory.getLog(loggerName);
    }

    class myArrayList<E> extends ArrayList<E> {

        @Override
        public String toString() {
            Iterator<E> it = iterator();
            if (!it.hasNext()) {
                return "";
            }

            StringBuilder sb = new StringBuilder();
            for (; ; ) {
                E e = it.next();
                sb.append(e == this ? "(this Collection)" : e);
                if (!it.hasNext()) {
                    return sb.toString();
                }
                sb.append(',').append(' ');
            }
        }
    }
}
