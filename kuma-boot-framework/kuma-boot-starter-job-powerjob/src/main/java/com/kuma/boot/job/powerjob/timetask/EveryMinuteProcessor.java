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

package com.kuma.boot.job.powerjob.timetask;

import com.kuma.boot.common.utils.log.LogUtils;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

import java.util.List;

/**
 * 每分钟定时任务处理器
 * <p>在 PowerJob 控制台配置 CRON 表达式 {@code 0 * * * * ?}，处理器为本类全限定名</p>
 *
 * @author kuma
 * @version 2024.1
 * @since 2024-01-01
 */
public class EveryMinuteProcessor implements BasicProcessor {

    private final List<EveryMinuteExecute> everyMinuteExecutes;

    public EveryMinuteProcessor(List<EveryMinuteExecute> everyMinuteExecutes) {
        this.everyMinuteExecutes = everyMinuteExecutes;
    }

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        LogUtils.info("每分钟任务执行");
        if (everyMinuteExecutes == null || everyMinuteExecutes.isEmpty()) {
            return new ProcessResult(true, "no tasks");
        }

        for (EveryMinuteExecute task : everyMinuteExecutes) {
            try {
                task.execute();
            } catch (Exception e) {
                LogUtils.error("每分钟任务异常: {}", e.getMessage(), e);
                context.getOmsLogger().error("每分钟任务异常: {} - {}", task.getClass().getSimpleName(), e.getMessage());
                return new ProcessResult(false, e.getMessage());
            }
        }
        return new ProcessResult(true, "success");
    }
}
