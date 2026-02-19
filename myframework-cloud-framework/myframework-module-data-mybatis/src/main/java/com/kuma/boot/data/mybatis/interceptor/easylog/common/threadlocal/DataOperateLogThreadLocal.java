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

package com.kuma.boot.data.mybatis.interceptor.easylog.common.threadlocal;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.kuma.boot.data.mybatis.interceptor.easylog.common.audit.DataAuditLogging;
import com.kuma.boot.data.mybatis.interceptor.easylog.common.model.UserCache;
import com.kuma.boot.data.mybatis.interceptor.easylog.common.utils.audit.OperationDataChange;

import java.util.List;

import org.aspectj.lang.JoinPoint;

/**
 * DataOperateLogThreadLocal
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class DataOperateLogThreadLocal {

    public static final String TRACE_ID = "threaddata_id";

    public static final String USER_TOKEN = "user_token";

    /**
     * 注解
     */
    public static final TransmittableThreadLocal<DataAuditLogging> DATA_LOG =
            new TransmittableThreadLocal<>();

    /**
     * 切点
     */
    public static final TransmittableThreadLocal<JoinPoint> JOIN_POINT =
            new TransmittableThreadLocal<>();

    /**
     * 数据变化
     */
    public static final TransmittableThreadLocal<List<OperationDataChange>> DATA_CHANGES =
            new TransmittableThreadLocal<>();

    /**
     * 当前线程操作生成的唯一ID
     */
    public static final TransmittableThreadLocal<String> THREADDATA_ID =
            new TransmittableThreadLocal<>();

    /**
     * 当前操作的调用的方法
     */
    public static final TransmittableThreadLocal<String> THREADDATA_METHOD =
            new TransmittableThreadLocal<>();

    /**
     * 当前操作的调用的方法
     */
    public static final TransmittableThreadLocal<UserCache> THREADDATA_USER_CACHE =
            new TransmittableThreadLocal<>();

    /**
     * 调用
     */
    public static void transfer() {
        DATA_CHANGES.remove();
        DATA_LOG.remove();
        JOIN_POINT.remove();
        THREADDATA_ID.remove();
        THREADDATA_METHOD.remove();
        THREADDATA_USER_CACHE.remove();
    }
}
