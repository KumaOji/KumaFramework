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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.model;

/**
 * 数据版本日志
 */
public class DataVersionLogData {
    /**
     * 表名称
     */
    private String tableName;

    /**
     * 数据名称
     */
    private String dataName;

    /**
     * 数据主键
     */
    private String dataId;

    /**
     * 数据内容对象
     */
    private Object dataContent;

    /**
     * 本次变动的数据内容
     */
    private Object changeContent;

    /**
     * 版本
     */
    private Integer version;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public Object getDataContent() {
        return dataContent;
    }

    public void setDataContent(Object dataContent) {
        this.dataContent = dataContent;
    }

    public Object getChangeContent() {
        return changeContent;
    }

    public void setChangeContent(Object changeContent) {
        this.changeContent = changeContent;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
