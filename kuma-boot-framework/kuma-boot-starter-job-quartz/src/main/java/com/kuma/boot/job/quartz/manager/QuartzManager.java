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

package com.kuma.boot.job.quartz.manager;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.job.common.exception.JobCommonException;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

import java.util.Map;

/**
 * Quartz 动态任务管理器。
 * <p>封装 {@link Scheduler} 提供任务的增删改查、暂停/恢复、立即触发等操作。</p>
 */
public class QuartzManager {

    private final Scheduler scheduler;

    public QuartzManager(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * 新增或更新一个 Cron 任务。若任务已存在则更新其 Cron 表达式。
     *
     * @param jobClass      Job 实现类
     * @param jobName       任务名称
     * @param jobGroup      任务分组
     * @param cronExpression Cron 表达式
     * @param jobDataMap    任务数据（可为 null）
     */
    public void addOrUpdateJob(
            Class<? extends Job> jobClass,
            String jobName,
            String jobGroup,
            String cronExpression,
            Map<String, Object> jobDataMap) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
            if (scheduler.checkExists(jobKey)) {
                updateJobCron(jobName, jobGroup, cronExpression);
                return;
            }

            JobBuilder jobBuilder = JobBuilder.newJob(jobClass)
                    .withIdentity(jobName, jobGroup)
                    .storeDurably();

            if (jobDataMap != null && !jobDataMap.isEmpty()) {
                jobBuilder.usingJobData(new JobDataMap(jobDataMap));
            }

            JobDetail jobDetail = jobBuilder.build();

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobName, jobGroup)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            LogUtils.info("Quartz addOrUpdateJob -> ({}/{}), cron: {}", jobGroup, jobName, cronExpression);
        } catch (SchedulerException e) {
            throw new JobCommonException("Quartz addOrUpdateJob failed: " + e.getMessage(), e);
        }
    }

    /**
     * 仅更新已有任务的 Cron 表达式。
     */
    public void updateJobCron(String jobName, String jobGroup, String cronExpression) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
            CronTrigger newTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();
            scheduler.rescheduleJob(triggerKey, newTrigger);
            LogUtils.info("Quartz updateJobCron -> ({}/{}), newCron: {}", jobGroup, jobName, cronExpression);
        } catch (SchedulerException e) {
            throw new JobCommonException("Quartz updateJobCron failed: " + e.getMessage(), e);
        }
    }

    /**
     * 删除任务（同时删除 Trigger）。
     */
    public void deleteJob(String jobName, String jobGroup) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
                LogUtils.info("Quartz deleteJob -> ({}/{})", jobGroup, jobName);
            }
        } catch (SchedulerException e) {
            throw new JobCommonException("Quartz deleteJob failed: " + e.getMessage(), e);
        }
    }

    /**
     * 暂停任务。
     */
    public void pauseJob(String jobName, String jobGroup) {
        try {
            scheduler.pauseJob(JobKey.jobKey(jobName, jobGroup));
            LogUtils.info("Quartz pauseJob -> ({}/{})", jobGroup, jobName);
        } catch (SchedulerException e) {
            throw new JobCommonException("Quartz pauseJob failed: " + e.getMessage(), e);
        }
    }

    /**
     * 恢复已暂停的任务。
     */
    public void resumeJob(String jobName, String jobGroup) {
        try {
            scheduler.resumeJob(JobKey.jobKey(jobName, jobGroup));
            LogUtils.info("Quartz resumeJob -> ({}/{})", jobGroup, jobName);
        } catch (SchedulerException e) {
            throw new JobCommonException("Quartz resumeJob failed: " + e.getMessage(), e);
        }
    }

    /**
     * 立即触发一次任务，不影响既有调度计划。
     */
    public void triggerJob(String jobName, String jobGroup) {
        try {
            scheduler.triggerJob(JobKey.jobKey(jobName, jobGroup));
            LogUtils.info("Quartz triggerJob -> ({}/{})", jobGroup, jobName);
        } catch (SchedulerException e) {
            throw new JobCommonException("Quartz triggerJob failed: " + e.getMessage(), e);
        }
    }

    /**
     * 判断任务是否存在。
     */
    public boolean existsJob(String jobName, String jobGroup) {
        try {
            return scheduler.checkExists(JobKey.jobKey(jobName, jobGroup));
        } catch (SchedulerException e) {
            throw new JobCommonException("Quartz existsJob failed: " + e.getMessage(), e);
        }
    }

    /**
     * 获取任务当前 Trigger 状态。
     */
    public Trigger.TriggerState getTriggerState(String jobName, String jobGroup) {
        try {
            return scheduler.getTriggerState(TriggerKey.triggerKey(jobName, jobGroup));
        } catch (SchedulerException e) {
            throw new JobCommonException("Quartz getTriggerState failed: " + e.getMessage(), e);
        }
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
}
