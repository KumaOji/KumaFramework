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

package com.kuma.boot.data.mybatis.interceptor.log.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 真实SQL日志的配置集合
 */
@ConfigurationProperties(prefix = "mybatis.actual.sql")
public class ActualSqlProperties {

    /**
     * 是否展示方法名
     */
    private boolean showMethod = true;

    /**
     * 是否展示SQL
     */
    private boolean showSql = true;

    /**
     * 是否展示执行耗时
     */
    private boolean showElapsed = true;

    /**
     * 是否展示结果行数
     */
    private boolean showRows = true;

    /**
     * 日志级别
     */
    private com.kuma.boot.data.mybatis.interceptor.log.properties.LogLevel logLevel = com.kuma.boot.data.mybatis.interceptor.log.properties.LogLevel.TRACE;

    public boolean isShowMethod() {
        return showMethod;
    }

    public void setShowMethod(boolean showMethod) {
        this.showMethod = showMethod;
    }

    public boolean isShowSql() {
        return showSql;
    }

    public void setShowSql(boolean showSql) {
        this.showSql = showSql;
    }

    public boolean isShowElapsed() {
        return showElapsed;
    }

    public void setShowElapsed(boolean showElapsed) {
        this.showElapsed = showElapsed;
    }

    public boolean isShowRows() {
        return showRows;
    }

    public void setShowRows(boolean showRows) {
        this.showRows = showRows;
    }

    public com.kuma.boot.data.mybatis.interceptor.log.properties.LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(com.kuma.boot.data.mybatis.interceptor.log.properties.LogLevel logLevel) {
        this.logLevel = logLevel;
    }
}
