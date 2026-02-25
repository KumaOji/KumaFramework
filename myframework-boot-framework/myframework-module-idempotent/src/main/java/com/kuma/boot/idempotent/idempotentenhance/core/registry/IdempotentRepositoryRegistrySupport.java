/*
 *  com.kuma.boot.common.utils.context.ContextUtils
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.jspecify.annotations.NonNull
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.context.ApplicationListener
 *  org.springframework.context.event.ContextRefreshedEvent
 */
package com.kuma.boot.idempotent.idempotentenhance.core.registry;

import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.idempotent.idempotentenhance.core.config.properties.IdempotentCoreProperties;
import com.kuma.boot.idempotent.idempotentenhance.core.exception.IdempotentException;
import com.kuma.boot.idempotent.idempotentenhance.core.repository.IdempotentRepository;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class IdempotentRepositoryRegistrySupport
implements ApplicationListener<ContextRefreshedEvent> {
    private final IdempotentCoreProperties properties;

    public IdempotentRepositoryRegistrySupport(IdempotentCoreProperties properties) {
        this.properties = properties;
    }

    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        Map repositories;
        try {
            repositories = ContextUtils.getBeansOfType(IdempotentRepository.class);
        }
        catch (NoSuchBeanDefinitionException ex) {
            throw new RuntimeException("Can not found any IdempotentRepository implements, please check config.");
        }
        LogUtils.info((String)"The number of IdempotentRepository is [{}], register it to registry now.", (Object[])new Object[]{repositories.size()});
        boolean onlyOneRepository = repositories.size() == 1;
        repositories.forEach((beanName, repository) -> {
            LogUtils.warn((String)"=================>>>>>>>>>>>>>>>>> Register [{}] to registry begin.", (Object[])new Object[]{beanName});
            if (onlyOneRepository) {
                IdempotentRepositoryRegistry.setPrimaryRepository(repository);
            }
            IdempotentRepositoryRegistry.register(beanName, repository);
            LogUtils.warn((String)"=================>>>>>>>>>>>>>>>>> Register [{}] to registry success.", (Object[])new Object[]{beanName});
        });
        if (Objects.isNull(IdempotentRepositoryRegistry.getPrimaryRepository())) {
            String primaryRepository = this.properties.getPrimaryRepository();
            if (StringUtils.isBlank((String)primaryRepository) || Objects.isNull(IdempotentRepositoryRegistry.find(primaryRepository))) {
                throw new IdempotentException("Can not found primaryRepository, please check config.");
            }
            IdempotentRepositoryRegistry.setPrimaryRepository(IdempotentRepositoryRegistry.find(primaryRepository));
        }
        LogUtils.warn((String)"Register IdempotentRepository to registry finished, register count is [{}], primaryRepository is [{}].", (Object[])new Object[]{IdempotentRepositoryRegistry.getIdempotentRepositories().size(), IdempotentRepositoryRegistry.getPrimaryRepository().getClass().getName()});
    }
}

