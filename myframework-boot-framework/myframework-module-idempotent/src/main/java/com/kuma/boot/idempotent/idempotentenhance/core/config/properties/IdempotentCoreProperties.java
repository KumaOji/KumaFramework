/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.idempotent.idempotentenhance.core.config.properties;

import com.google.common.base.Preconditions;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.TimeUnit;

/** 幂等核心properties */
@ConfigurationProperties(prefix = IdempotentCoreProperties.PREFIX)
public class IdempotentCoreProperties implements ApplicationListener<ContextRefreshedEvent> {

    public static final String PREFIX = "enhance.idempotent";

    /** 业务最大处理时间，默认值为-1，表示失败数据永远不自愈，等待人工处理 */
    private Long maxProcessTime = -1L;

    /** 最大处理时间单位（默认秒） */
    private TimeUnit unit = TimeUnit.SECONDS;

    /** 命名空间（用于应用隔离） */
    private String namespace;

    /** primary idempotent repository TODO 留给后面同时支持多个幂等实现，并可以动态选择幂等实现时 */
    private String primaryRepository;

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent contextRefreshedEvent) {
        if (StringUtils.isBlank(namespace)) {
            ApplicationContext context = contextRefreshedEvent.getApplicationContext();
            namespace = context.getEnvironment().getProperty("spring.application.name");
            Preconditions.checkArgument(
                    StringUtils.isNotBlank(namespace),
                    "can not found namespace, please check config [enhance.idempotent.namespace] or"
                            + " [spring.application.name]");
        }
        LogUtils.info("idempotent core namespace is [{}].", namespace);
    }

    public Long getMaxProcessTime() {
        return maxProcessTime;
    }

    public void setMaxProcessTime(Long maxProcessTime) {
        this.maxProcessTime = maxProcessTime;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getPrimaryRepository() {
        return primaryRepository;
    }

    public void setPrimaryRepository(String primaryRepository) {
        this.primaryRepository = primaryRepository;
    }
}
