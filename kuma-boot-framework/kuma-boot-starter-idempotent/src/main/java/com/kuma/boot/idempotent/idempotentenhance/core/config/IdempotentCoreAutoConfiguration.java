package com.kuma.boot.idempotent.idempotentenhance.core.config;

import com.kuma.boot.idempotent.idempotentenhance.core.aspect.IdempotentAspect;
import com.kuma.boot.idempotent.idempotentenhance.core.config.properties.IdempotentCoreProperties;
import com.kuma.boot.idempotent.idempotentenhance.core.handler.DefaultIdempotentExceptionEventHandler;
import com.kuma.boot.idempotent.idempotentenhance.core.handler.IdempotentExceptionEventHandler;
import com.kuma.boot.idempotent.idempotentenhance.core.helper.IdempotentTemplate;
import com.kuma.boot.idempotent.idempotentenhance.core.registry.IdempotentRepositoryRegistrySupport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.jspecify.annotations.NonNull;

/**
 * <p>
 * 幂等核心自动配置
 * </p>
 *
 * @author wenpan 2023/01/04 21:56
 */
@Configuration
@ConditionalOnProperty(prefix = "enhance.idempotent", name = {"enable"}, value = {"true"})
@EnableConfigurationProperties({IdempotentCoreProperties.class})
public class IdempotentCoreAutoConfiguration {

//    @Bean("applicationContextHelper")
//    public ApplicationContextHelper applicationContextHelper() {
//        return new ApplicationContextHelper();
//    }

    @Bean
    @DependsOn({"idempotentHelper"})
    @ConditionalOnMissingBean({IdempotentAspect.class})
    public IdempotentAspect idempotentAspect() {
        return new IdempotentAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public IdempotentExceptionEventHandler defaultIdempotentExceptionEventHandler() {
        return new DefaultIdempotentExceptionEventHandler();
    }

    @Bean
    public IdempotentRepositoryRegistrySupport idempotentRepositoryRegistrySupport(IdempotentCoreProperties properties) {
        return new IdempotentRepositoryRegistrySupport(properties);
    }

    @Bean("idempotentTemplate")
    @ConditionalOnMissingBean
    public IdempotentTemplate idempotentTemplate(@NonNull IdempotentExceptionEventHandler exceptionEventHandler,
                                                 @NonNull IdempotentCoreProperties idempotentCoreProperties) {
        return new IdempotentTemplate(exceptionEventHandler, idempotentCoreProperties);
    }

}
