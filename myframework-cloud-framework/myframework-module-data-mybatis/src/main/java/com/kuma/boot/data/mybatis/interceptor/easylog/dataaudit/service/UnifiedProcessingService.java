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

package com.kuma.boot.data.mybatis.interceptor.easylog.dataaudit.service;

import com.alibaba.fastjson2.JSON;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mybatis.interceptor.easylog.common.threadlocal.DataOperateLogThreadLocal;
import com.kuma.boot.data.mybatis.interceptor.easylog.common.utils.UUIDUtils;
import com.kuma.boot.data.mybatis.interceptor.easylog.common.utils.audit.OperationDataChange;
import com.kuma.boot.data.mybatis.interceptor.easylog.dataaudit.model.MongoDataAuditRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * UnifiedProcessingService
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@Service
public class UnifiedProcessingService {

    private Logger logger = LoggerFactory.getLogger(UnifiedProcessingService.class);

    private final String openapiUrl = "http://MongoDB服务/";

    /**
     * 对比保存数据
     */
    @Async
    public void compareAndTransfer( List<OperationDataChange> list ) {
        List<MongoDataAuditRecord> saveData = new ArrayList<>();
        list.forEach(
                change -> {
                    if (change.getSqlCommandType().equals(SqlCommandType.SELECT)) {
                        List<?> oldData = change.getOldData();
                        List<?> newData = change.getNewData();
                        // 更新前后数据量不对必定是删除（逻辑删除）不做处理
                        if (newData == null) {
                            return;
                        }
                        if (oldData == null) {
                            return;
                        }
                        if (oldData.size() != newData.size()) {
                            return;
                        }

                        for (int i = 0; i < oldData.size(); i++) {
                            try {
                                saveData.add(
                                        new MongoDataAuditRecord()
                                                .converterMongoDataAuditRecord(
                                                        change, oldData.get(i), newData.get(i)));
                            } catch (Exception e) {
                                LogUtils.error(e);
                            }
                        }
                    } else {
                        MongoDataAuditRecord record = new MongoDataAuditRecord();
                        record.setNewObject(change.getQuerySql());
                        record.setOperatorId(
                                DataOperateLogThreadLocal.THREADDATA_USER_CACHE.get().getUserId());
                        record.setBusinessProcessId(DataOperateLogThreadLocal.THREADDATA_ID.get());
                        record.setId(UUIDUtils.getStringUUID());
                        record.setBusinessCallMethod(
                                DataOperateLogThreadLocal.THREADDATA_METHOD.get());
                        record.setCreateTime(new Date());
                        record.setSqlCommandType(change.getSqlCommandType());
                        saveData.add(record);
                    }
                });

        // 保存数据
        if (!CollectionUtils.isEmpty(saveData)) {
            batchAddOperateLog(saveData);
        }

        // 删除此线程局部变量的当前线程
        DataOperateLogThreadLocal.transfer();
    }

    /**
     * 批量插入
     */
    public void batchAddOperateLog( List<?> list ) {
        try {
            LogUtils.info("变更数据---> {}", list);
            //			JSONObject jsonObject = new JSONObject();
            //			jsonObject.put("list", list);
            //			String url = openapiUrl + "/apiMongo/batchAddOperationData";
            //
            //			logger.info("请求地址:{},{}", url, JSON.toJSONString(list));
            //
            //			final HttpHeaders headers = new HttpHeaders();
            //			headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
            //			final HttpEntity<Object> entity = new HttpEntity<>(jsonObject, headers);
            //			restTemplate.postForObject(url, entity, String.class);
        } catch (Exception e) {
            logger.info("数据审计记录失败:{}", JSON.toJSONString(e));
        }
    }
}
