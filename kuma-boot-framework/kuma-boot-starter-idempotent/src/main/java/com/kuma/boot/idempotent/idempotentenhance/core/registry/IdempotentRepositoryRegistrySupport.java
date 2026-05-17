/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.idempotent.idempotentenhance.core.registry;

import com.kuma.boot.core.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.idempotent.idempotentenhance.core.config.properties.IdempotentCoreProperties;
import com.kuma.boot.idempotent.idempotentenhance.core.exception.IdempotentException;
import com.kuma.boot.idempotent.idempotentenhance.core.repository.IdempotentRepository;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Objects;

/** 发现所有可用的IdempotentRepository实现，并将这些 IdempotentRepository 实现都注入本地注册中心 */
public class IdempotentRepositoryRegistrySupport implements ApplicationListener<ContextRefreshedEvent> {

    private final IdempotentCoreProperties properties;

    public IdempotentRepositoryRegistrySupport(IdempotentCoreProperties properties) {
        this.properties = properties;
    }

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        Map<String, IdempotentRepository> repositories;
        try {
            repositories = ContextUtils.getBeansOfType(IdempotentRepository.class);
        } catch (NoSuchBeanDefinitionException ex) {
            throw new RuntimeException("Can not found any IdempotentRepository implements, please check config.");
        }
        LogUtils.info("The number of IdempotentRepository is [{}], register it to registry now.", repositories.size());
        boolean onlyOneRepository = repositories.size() == 1;
        // 发现 IdempotentRepository 并注入注册中心
        repositories.forEach((beanName, repository) -> {
            LogUtils.warn("=================>>>>>>>>>>>>>>>>> Register [{}] to registry begin.", beanName);
            // 如果只有一个 IdempotentRepository 则这个 repository 就是 primary
            if (onlyOneRepository) {
                IdempotentRepositoryRegistry.setPrimaryRepository(repository);
            }
            IdempotentRepositoryRegistry.register(beanName, repository);
            LogUtils.warn("=================>>>>>>>>>>>>>>>>> Register [{}] to registry success.", beanName);
        });
        // 设置 PrimaryRepository
        if (Objects.isNull(IdempotentRepositoryRegistry.getPrimaryRepository())) {
            // 获取配置文件中指定的 PrimaryRepository
            String primaryRepository = properties.getPrimaryRepository();
            if (StringUtils.isBlank(primaryRepository)
                    || Objects.isNull(IdempotentRepositoryRegistry.find(primaryRepository))) {
                throw new IdempotentException("Can not found primaryRepository, please check config.");
            }
            // 注册默认的repository
            IdempotentRepositoryRegistry.setPrimaryRepository(IdempotentRepositoryRegistry.find(primaryRepository));
        }
        LogUtils.warn(
                "Register IdempotentRepository to registry finished, register count is [{}],"
                        + " primaryRepository is [{}].",
                IdempotentRepositoryRegistry.getIdempotentRepositories().size(),
                IdempotentRepositoryRegistry.getPrimaryRepository().getClass().getName());
    }
}
