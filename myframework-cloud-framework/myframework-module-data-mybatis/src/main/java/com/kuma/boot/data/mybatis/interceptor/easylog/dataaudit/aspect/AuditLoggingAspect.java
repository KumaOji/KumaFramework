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

package com.kuma.boot.data.mybatis.interceptor.easylog.dataaudit.aspect;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.kuma.boot.common.holder.UserContextHolder;
import com.kuma.boot.common.utils.servlet.TraceUtils;
import com.kuma.boot.data.mybatis.interceptor.easylog.common.audit.DataAuditLogging;
import com.kuma.boot.data.mybatis.interceptor.easylog.common.model.UserCache;
import com.kuma.boot.data.mybatis.interceptor.easylog.common.threadlocal.DataOperateLogThreadLocal;
import com.kuma.boot.data.mybatis.interceptor.easylog.common.utils.audit.OperationDataChange;
import com.kuma.boot.data.mybatis.interceptor.easylog.dataaudit.service.UnifiedProcessingService;

import java.util.LinkedList;
import java.util.List;

import org.apache.ibatis.mapping.SqlCommandType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

/**
 * AuditLoggingAspect
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@Aspect
@Component
public class AuditLoggingAspect {

    private Logger logger = LoggerFactory.getLogger(AuditLoggingAspect.class);

    @Autowired
    private UnifiedProcessingService unifiedProcessingService;

    /**
     * 数据审核日志切面前执行
     */
    @Before("@annotation(dataLog)")
    public void beforeDataAuditLogging( JoinPoint joinPoint, DataAuditLogging dataLog ) {
        String obj = DataOperateLogThreadLocal.THREADDATA_ID.get();
        if (obj == null) {
            UserCache cache = DataOperateLogThreadLocal.THREADDATA_USER_CACHE.get();
            if (cache != null) {
                obj = cache.getRequestId();
            } else {
                UserCache userCache = new UserCache();
                userCache.setUserId(String.valueOf(UserContextHolder.getUserId()));
                userCache.setUserId(UserContextHolder.getUserName());
                userCache.setRequestId(TraceUtils.getKmcTraceId());
                DataOperateLogThreadLocal.THREADDATA_USER_CACHE.set(userCache);

                obj = userCache.getRequestId();
            }
        }
        DataOperateLogThreadLocal.THREADDATA_ID.set(obj);

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringTypeName();
        DataOperateLogThreadLocal.THREADDATA_METHOD.set(
                className + "#" + joinPoint.getSignature().getName());
        DataOperateLogThreadLocal.DATA_CHANGES.set(new LinkedList<>());
        DataOperateLogThreadLocal.JOIN_POINT.set(joinPoint);
        DataOperateLogThreadLocal.DATA_LOG.set(dataLog);
    }

    /**
     * 数据审计日志切面后执行
     */
    @AfterReturning("@annotation(dataLog)")
    public void afterDataAuditLogging( DataAuditLogging dataLog ) {
        try {
            List<OperationDataChange> list = DataOperateLogThreadLocal.DATA_CHANGES.get();
            if (CollectionUtils.isEmpty(list)) {
                return;
            }

            list.forEach(
                    change -> {
                        if (change.getSqlCommandType().equals(SqlCommandType.SELECT)) {
                            List<?> oldData = change.getOldData();
                            if (CollectionUtils.isEmpty(oldData)) {
                                return;
                            }

                            List<Object> maps =
                                    change.getJdbcTemplate()
                                            .query(
                                                    change.getQuerySql(),
                                                    new BeanPropertyRowMapper(
                                                            change.getEntityType()));
                            change.setNewData(maps);
                        }
                    });

            unifiedProcessingService.compareAndTransfer(list);
        } catch (Exception e) {
            logger.info("数据审计日志记录失败:{}", JSON.toJSONString(e));
        }
    }
}
