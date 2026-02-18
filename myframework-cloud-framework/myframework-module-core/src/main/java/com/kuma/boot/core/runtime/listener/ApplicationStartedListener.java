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

package com.kuma.boot.core.runtime.listener;

import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.Duration;

/**
 * 服务启动成功事件
 *
 * @author kuma
 * @version 2022.05
 * @since 2022-05-30 13:29:36
 */
public class ApplicationStartedListener implements ApplicationListener<ApplicationStartedEvent> {

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        Duration timeTaken = event.getTimeTaken();

        LogUtils.info("Application [{}] Started StartupDate: {} TimeTaken: {} s, ApplicationType:{}",
                event.getApplicationContext().getApplicationName(),
                DateUtils.formatTimestamp(event.getApplicationContext().getStartupDate()),
                timeTaken == null? 0: timeTaken.toSeconds(),
                event.getSpringApplication().getWebApplicationType());

        // 如果是配置中心的上下文略过，spring cloud环境environment会读取不到
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        if (applicationContext instanceof AnnotationConfigApplicationContext) {
            return;
        }

        // 执行具体业务
        this.eventCallback(event);
    }

    /**
     * 监听器具体的业务逻辑
     */
    protected void eventCallback(ApplicationStartedEvent event) {}
}
