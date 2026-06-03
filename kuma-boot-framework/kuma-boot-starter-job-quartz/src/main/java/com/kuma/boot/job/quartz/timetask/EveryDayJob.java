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

package com.kuma.boot.job.quartz.timetask;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.job.quartz.core.BaseQuartzJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 每日定时 Quartz Job（Cron: {@code 0 0 0 * * ?}）。
 * <p>依次调用所有 {@link EveryDayExecute} 实现。</p>
 */
@DisallowConcurrentExecution
public class EveryDayJob extends BaseQuartzJob {

    @Autowired(required = false)
    private List<EveryDayExecute> everyDayExecutes;

    @Override
    protected void doExecute(JobExecutionContext context) {
        LogUtils.info("每日任务执行");
        if (everyDayExecutes == null || everyDayExecutes.isEmpty()) {
            return;
        }
        for (EveryDayExecute task : everyDayExecutes) {
            try {
                task.execute();
            } catch (Exception e) {
                LogUtils.error("每日任务异常 [{}]: {}", task.getClass().getSimpleName(), e.getMessage(), e);
                throw e;
            }
        }
    }
}
