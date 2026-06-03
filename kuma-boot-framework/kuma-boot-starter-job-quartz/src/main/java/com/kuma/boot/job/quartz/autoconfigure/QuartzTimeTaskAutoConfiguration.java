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

package com.kuma.boot.job.quartz.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.job.quartz.autoconfigure.properties.QuartzProperties;
import com.kuma.boot.job.quartz.timetask.EveryDayJob;
import com.kuma.boot.job.quartz.timetask.EveryHourJob;
import com.kuma.boot.job.quartz.timetask.EveryMinuteJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Quartz 内置定时任务自动配置。
 * <p>注册每分钟 / 每小时 / 每日三个定时 Job，收集对应接口的所有 Spring Bean 并按序执行。</p>
 */
@AutoConfiguration(after = QuartzAutoConfiguration.class)
@ConditionalOnClass(Scheduler.class)
@ConditionalOnProperty(prefix = QuartzProperties.PREFIX, name = "enabled", havingValue = "true")
public class QuartzTimeTaskAutoConfiguration implements InitializingBean {

    private static final String GROUP = "kuma-timetask";

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(QuartzTimeTaskAutoConfiguration.class, "kuma-boot-starter-job-quartz");
    }

    // ── Every-Minute ─────────────────────────────────────────────────────────

    @Bean
    @ConditionalOnProperty(prefix = QuartzProperties.PREFIX, name = "time-task", havingValue = "true", matchIfMissing = true)
    public JobDetail everyMinuteJobDetail() {
        return JobBuilder.newJob(EveryMinuteJob.class)
                .withIdentity("everyMinuteJob", GROUP)
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = QuartzProperties.PREFIX, name = "time-task", havingValue = "true", matchIfMissing = true)
    public Trigger everyMinuteTrigger(JobDetail everyMinuteJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(everyMinuteJobDetail)
                .withIdentity("everyMinuteTrigger", GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?"))
                .build();
    }

    // ── Every-Hour ────────────────────────────────────────────────────────────

    @Bean
    @ConditionalOnProperty(prefix = QuartzProperties.PREFIX, name = "time-task", havingValue = "true", matchIfMissing = true)
    public JobDetail everyHourJobDetail() {
        return JobBuilder.newJob(EveryHourJob.class)
                .withIdentity("everyHourJob", GROUP)
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = QuartzProperties.PREFIX, name = "time-task", havingValue = "true", matchIfMissing = true)
    public Trigger everyHourTrigger(JobDetail everyHourJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(everyHourJobDetail)
                .withIdentity("everyHourTrigger", GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * * * ?"))
                .build();
    }

    // ── Every-Day ─────────────────────────────────────────────────────────────

    @Bean
    @ConditionalOnProperty(prefix = QuartzProperties.PREFIX, name = "time-task", havingValue = "true", matchIfMissing = true)
    public JobDetail everyDayJobDetail() {
        return JobBuilder.newJob(EveryDayJob.class)
                .withIdentity("everyDayJob", GROUP)
                .storeDurably()
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = QuartzProperties.PREFIX, name = "time-task", havingValue = "true", matchIfMissing = true)
    public Trigger everyDayTrigger(JobDetail everyDayJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(everyDayJobDetail)
                .withIdentity("everyDayTrigger", GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 * * ?"))
                .build();
    }
}
