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

package com.kuma.boot.job.powerjob.core;

import com.alibaba.fastjson2.JSON;
import com.kuma.boot.common.utils.log.LogUtils;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import org.springframework.util.StringUtils;

/**
 * PowerJob 处理器基类
 *
 * @author kuma
 * @version 2024.1
 * @since 2024-01-01
 */
public abstract class BasePowerJobProcessor implements BasicProcessor {

    @Override
    public abstract ProcessResult process(TaskContext context) throws Exception;

    /**
     * 解析 JobParams 为指定类型对象
     */
    protected <T> T parseParams(TaskContext context, Class<T> clazz) {
        String params = context.getJobParams();
        OmsLogger omsLogger = context.getOmsLogger();

        omsLogger.info("请求参数: {}", params);
        LogUtils.info("请求参数: {}", params);

        if (!StringUtils.hasText(params)) {
            return JSON.parseObject("{}", clazz);
        }

        try {
            return JSON.parseObject(params, clazz);
        } catch (Exception e) {
            LogUtils.error("请求参数转换异常: {}", e.getMessage(), e);
            omsLogger.error("请求参数转换异常: {}", e.getMessage());
            return JSON.parseObject("{}", clazz);
        }
    }

    protected ProcessResult success() {
        return new ProcessResult(true, "success");
    }

    protected ProcessResult success(String message) {
        return new ProcessResult(true, message);
    }

    protected ProcessResult fail(String message) {
        return new ProcessResult(false, message);
    }

    protected ProcessResult fail(Exception e) {
        return new ProcessResult(false, e.getMessage());
    }
}
