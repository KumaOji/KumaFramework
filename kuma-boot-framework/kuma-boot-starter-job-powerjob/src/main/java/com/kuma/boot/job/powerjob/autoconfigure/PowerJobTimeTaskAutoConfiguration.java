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

package com.kuma.boot.job.powerjob.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.job.powerjob.autoconfigure.properties.PowerJobProperties;
import com.kuma.boot.job.powerjob.timetask.EveryDayExecute;
import com.kuma.boot.job.powerjob.timetask.EveryDayProcessor;
import com.kuma.boot.job.powerjob.timetask.EveryHourExecute;
import com.kuma.boot.job.powerjob.timetask.EveryHourProcessor;
import com.kuma.boot.job.powerjob.timetask.EveryMinuteExecute;
import com.kuma.boot.job.powerjob.timetask.EveryMinuteProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * PowerJob 定时任务自动配置
 *
 * @author kuma
 * @version 2024.1
 * @since 2024-01-01
 */
@AutoConfiguration(after = PowerJobAutoConfiguration.class)
@ConditionalOnProperty(prefix = PowerJobProperties.PREFIX, name = "enabled", havingValue = "true")
public class PowerJobTimeTaskAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(PowerJobTimeTaskAutoConfiguration.class,
                "kuma-boot-starter-job-powerjob");
    }

    @Bean
    public EveryMinuteProcessor everyMinuteProcessor(
            ObjectProvider<List<EveryMinuteExecute>> everyMinuteExecutes) {
        return new EveryMinuteProcessor(everyMinuteExecutes.getIfAvailable(ArrayList::new));
    }

    @Bean
    public EveryHourProcessor everyHourProcessor(
            ObjectProvider<List<EveryHourExecute>> everyHourExecutes) {
        return new EveryHourProcessor(everyHourExecutes.getIfAvailable(ArrayList::new));
    }

    @Bean
    public EveryDayProcessor everyDayProcessor(
            ObjectProvider<List<EveryDayExecute>> everyDayExecutes) {
        return new EveryDayProcessor(everyDayExecutes.getIfAvailable(ArrayList::new));
    }
}
