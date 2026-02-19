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

package com.kuma.boot.data.mybatis.interceptor.easylog.common.utils.audit;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * OperationDataChange
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class OperationDataChange implements Serializable {

    /**
     * 当前数据变更的流程ID
     */
    private String dataId;

    /**
     * jdbctemplate
     */
    private JdbcTemplate jdbcTemplate;

    /**
     * sqlStatement
     */
    private String querySql;

    /**
     * 表名
     */
    private String tableName;

    /**
     * where 条件
     */
    private String whereSql;

    /**
     * 对应实体类
     */
    private Class<?> entityType;

    /**
     * 更新前数据
     */
    private List<?> oldData;

    /**
     * 更新后数据
     */
    private List<?> newData;

    /**
     * 传递的数据
     */
    private List<?> transferData;

    private SqlCommandType sqlCommandType;

    public List<?> getTransferData() {
        return transferData;
    }

    public void setTransferData( List<?> transferData ) {
        this.transferData = transferData;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId( String dataId ) {
        this.dataId = dataId;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getQuerySql() {
        return querySql;
    }

    public void setQuerySql( String querySql ) {
        this.querySql = querySql;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName( String tableName ) {
        this.tableName = tableName;
    }

    public String getWhereSql() {
        return whereSql;
    }

    public void setWhereSql( String whereSql ) {
        this.whereSql = whereSql;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public void setEntityType( Class<?> entityType ) {
        this.entityType = entityType;
    }

    public List<?> getOldData() {
        return oldData;
    }

    public void setOldData( List<?> oldData ) {
        this.oldData = oldData;
    }

    public List<?> getNewData() {
        return newData;
    }

    public void setNewData( List<?> newData ) {
        this.newData = newData;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public void setSqlCommandType( SqlCommandType sqlCommandType ) {
        this.sqlCommandType = sqlCommandType;
    }
}
