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

package com.kuma.boot.job.quartz.core;

import com.kuma.boot.common.utils.log.LogUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.StringUtils;

/**
 * Quartz Job 基类。
 * <p>继承此类并实现 {@link #executeInternal} 完成业务逻辑；
 * 通过 {@link #getParam} 从 JobDataMap 中读取参数。</p>
 */
public abstract class BaseQuartzJob extends QuartzJobBean {

    private static final String JOB_PARAM_KEY = "jobParam";

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            doExecute(context);
        } catch (JobExecutionException e) {
            throw e;
        } catch (Exception e) {
            LogUtils.error("Quartz job execute error -> ({}): {}", getClass().getSimpleName(), e.getMessage(), e);
            throw new JobExecutionException(e);
        }
    }

    /**
     * 业务执行入口，由子类实现。
     */
    protected abstract void doExecute(JobExecutionContext context) throws Exception;

    /**
     * 从 JobDataMap 中获取指定 key 的字符串参数，优先取 merged map。
     */
    protected String getParam(JobExecutionContext context, String key) {
        JobDataMap dataMap = context.getMergedJobDataMap();
        return dataMap.getString(key);
    }

    /**
     * 从 merged JobDataMap 中获取标准 jobParam 字段。
     */
    protected String getJobParam(JobExecutionContext context) {
        String param = getParam(context, JOB_PARAM_KEY);
        return StringUtils.hasText(param) ? param : "";
    }
}
