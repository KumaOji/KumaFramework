package com.kuma.boot.idempotent.idempotentenhance.core.registry;

import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import org.apache.commons.lang3.StringUtils;
import com.kuma.boot.idempotent.idempotentenhance.core.config.properties.IdempotentCoreProperties;
import com.kuma.boot.idempotent.idempotentenhance.core.exception.IdempotentException;
import com.kuma.boot.idempotent.idempotentenhance.core.repository.IdempotentRepository;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Objects;

/**
 * 发现所有可用的IdempotentRepository实现，并将这些 IdempotentRepository 实现都注入本地注册中心
 *
 * @author wenpan 2023/01/08 22:13
 */
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
                IdempotentRepositoryRegistry.registerPrimary(repository);
            }
            IdempotentRepositoryRegistry.register(beanName, repository);
            LogUtils.warn("=================>>>>>>>>>>>>>>>>> Register [{}] to registry success.", beanName);
        });
        // 设置 PrimaryRepository (多个 repository 实现时必须要在配置中明确的指定哪个 repository 是 primary
        // @see org.enhance.idempotent.core.config.properties.IdempotentCoreProperties.primaryRepository)
        if (Objects.isNull(IdempotentRepositoryRegistry.getPrimaryRepository())) {
            // 获取配置文件中指定的 PrimaryRepository
            String primaryRepository = properties.getPrimaryRepository();
            if (StringUtils.isBlank(primaryRepository)
                    || Objects.isNull(IdempotentRepositoryRegistry.find(primaryRepository))) {
                throw new IdempotentException("Can not found primaryRepository, please check config.");
            }
            // 注册默认的repository
            IdempotentRepositoryRegistry.registerPrimary(IdempotentRepositoryRegistry.find(primaryRepository));
        }
        LogUtils.warn("Register IdempotentRepository to registry finished, register count is [{}], primaryRepository is [{}].",
                IdempotentRepositoryRegistry.listIdempotentRepositories().size(),
                IdempotentRepositoryRegistry.getPrimaryRepository().getClass().getName());
    }
}
