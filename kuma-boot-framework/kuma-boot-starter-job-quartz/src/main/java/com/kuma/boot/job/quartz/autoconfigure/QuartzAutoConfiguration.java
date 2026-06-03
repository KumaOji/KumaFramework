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

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.job.quartz.autoconfigure.properties.QuartzProperties;
import com.kuma.boot.job.quartz.manager.QuartzManager;
import org.quartz.Scheduler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Quartz 自动配置入口。
 * <p>启用条件：{@code kuma.boot.job.quartz.enabled=true}</p>
 */
@AutoConfiguration
@ConditionalOnClass(Scheduler.class)
@ConditionalOnProperty(prefix = QuartzProperties.PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties(QuartzProperties.class)
public class QuartzAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(QuartzAutoConfiguration.class, StarterNameConstants.JOB_QUARTZ_STARTER);
    }

    /**
     * 动态任务管理器，封装 {@link Scheduler} 提供任务增删改查操作。
     * Spring Boot Quartz 自动配置会注入 {@link Scheduler} bean。
     */
    @Bean
    public QuartzManager quartzManager(Scheduler scheduler) {
        return new QuartzManager(scheduler);
    }
}
