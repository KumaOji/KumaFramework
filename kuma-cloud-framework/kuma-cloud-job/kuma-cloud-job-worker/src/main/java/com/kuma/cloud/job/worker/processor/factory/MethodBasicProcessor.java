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

package com.kuma.cloud.job.worker.processor.factory;

import com.kuma.cloud.job.common.utils.JsonUtils;
import com.kuma.cloud.job.worker.processor.ProcessResult;
import com.kuma.cloud.job.worker.processor.task.TaskContext;
import com.kuma.cloud.job.worker.processor.type.BasicProcessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * MethodBasicProcessor
 *
 * @author kuma
 * @version 2026.02
 * @since 2025-12-19 09:30:45
 */
class MethodBasicProcessor implements BasicProcessor {

    private final Object bean;

    private final Method method;

    public MethodBasicProcessor( Object bean, Method method ) {
        this.bean = bean;
        this.method = method;
    }

    @Override
    public ProcessResult process( TaskContext context ) throws Exception {
        try {
            Object result = method.invoke(bean, context);
            return new ProcessResult(true, JsonUtils.toJSONString(result));
        } catch (InvocationTargetException ite) {
            ExceptionUtils.rethrow(ite.getTargetException());
        }

        return new ProcessResult(false, "IMPOSSIBLE");
    }
}
