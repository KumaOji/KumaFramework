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

package com.kuma.boot.data.mybatis.interceptor.easylog.dataaudit.model;

import com.alibaba.fastjson2.JSON;
import com.kuma.boot.data.mybatis.interceptor.easylog.common.model.UserCache;
import com.kuma.boot.data.mybatis.interceptor.easylog.common.threadlocal.DataOperateLogThreadLocal;
import com.kuma.boot.data.mybatis.interceptor.easylog.common.utils.UUIDUtils;
import com.kuma.boot.data.mybatis.interceptor.easylog.common.utils.audit.CompareObjUtil;
import com.kuma.boot.data.mybatis.interceptor.easylog.common.utils.audit.OperationDataChange;

import java.io.Serializable;
import java.util.Date;

import org.apache.ibatis.mapping.SqlCommandType;

/**
 * MongoDataAuditRecord
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class MongoDataAuditRecord implements Serializable {

    private String id;

    /**
     * 操作业务流程ID
     */
    private String businessProcessId;

    /**
     * 变更之后数据发生的变更
     */
    private String changeData;

    /**
     * 传递的值
     */
    private String transferData;

    /**
     * 原数据值
     */
    private String oldObject;

    /**
     * 新数据值
     */
    private String newObject;

    /**
     * 操作的表明
     */
    private String tableName;

    /**
     * 业务调用的方法 模型
     */
    private String businessCallMethod;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 操作人ID
     */
    private String operatorId;

    private Integer source;

    private SqlCommandType sqlCommandType;

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public void setSqlCommandType( SqlCommandType sqlCommandType ) {
        this.sqlCommandType = sqlCommandType;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId( String operatorId ) {
        this.operatorId = operatorId;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource( Integer source ) {
        this.source = source;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime( Date createTime ) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getChangeData() {
        return changeData;
    }

    public void setChangeData( String changeData ) {
        this.changeData = changeData;
    }

    public String getTransferData() {
        return transferData;
    }

    public void setTransferData( String transferData ) {
        this.transferData = transferData;
    }

    public String getOldObject() {
        return oldObject;
    }

    public void setOldObject( String oldObject ) {
        this.oldObject = oldObject;
    }

    public String getNewObject() {
        return newObject;
    }

    public void setNewObject( String newObject ) {
        this.newObject = newObject;
    }

    public String getBusinessProcessId() {
        return businessProcessId;
    }

    public void setBusinessProcessId( String businessProcessId ) {
        this.businessProcessId = businessProcessId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName( String tableName ) {
        this.tableName = tableName;
    }

    public String getBusinessCallMethod() {
        return businessCallMethod;
    }

    public void setBusinessCallMethod( String businessCallMethod ) {
        this.businessCallMethod = businessCallMethod;
    }

    /**
     * 转换数据
     */
    public MongoDataAuditRecord converterMongoDataAuditRecord(
            OperationDataChange change, Object oldData, Object newData ) {
        String oldDataStr = JSON.toJSONString(oldData);
        String newDataStr = JSON.toJSONString(newData);
        String json = CompareObjUtil.campareJsonObject(oldDataStr, newDataStr);

        MongoDataAuditRecord operationRecord = new MongoDataAuditRecord();
        operationRecord.setChangeData(json);
        operationRecord.setTransferData(JSON.toJSONString(change.getTransferData()));
        operationRecord.setTableName(change.getTableName());
        operationRecord.setBusinessProcessId(DataOperateLogThreadLocal.THREADDATA_ID.get());
        operationRecord.setId(UUIDUtils.getStringUUID());
        operationRecord.setOldObject(oldDataStr);
        operationRecord.setNewObject(newDataStr);
        operationRecord.setBusinessCallMethod(DataOperateLogThreadLocal.THREADDATA_METHOD.get());
        operationRecord.setCreateTime(new Date());

        UserCache cache = DataOperateLogThreadLocal.THREADDATA_USER_CACHE.get();
        if (cache != null) {
            operationRecord.setOperatorId(cache.getUserId());
            // operationRecord.setSource(cache.getCallSuorce());
        }

        return operationRecord;
    }
}
