package com.kuma.boot.idempotent.idempotentenhance.adapter.redis.config;

import com.kuma.boot.idempotent.idempotentenhance.adapter.redis.RedisIdempotentRepositoryImpl;
import com.kuma.boot.idempotent.idempotentenhance.adapter.redis.config.properties.IdempotentRedisAdapterProperties;
import com.kuma.boot.idempotent.idempotentenhance.core.config.IdempotentCoreAutoConfiguration;
import com.kuma.boot.idempotent.idempotentenhance.core.config.properties.IdempotentCoreProperties;
import com.kuma.boot.idempotent.idempotentenhance.core.repository.IdempotentRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.jspecify.annotations.NonNull;

/**
 * 基于redis做幂等自动配置
 *
 * @author wenpanfeng 2023/01/05 11:42
 */
@Configuration
@EnableConfigurationProperties({IdempotentRedisAdapterProperties.class})
@AutoConfigureAfter({IdempotentCoreAutoConfiguration.class})
@ConditionalOnProperty(prefix = IdempotentCoreProperties.PREFIX,
        name = {"enable", "adapter.redis.enable"}, havingValue = "true")
public class IdempotentAdapterRedisAutoConfiguration {

    /**
     * 当容器中没有名称为 idempotentStringRedisTemplate 的bean时，往容器里注入一个默认Redis数据源的StringRedisTemplate，
     * 当容器中有名称为 idempotentStringRedisTemplate 的StringRedisTemplate时便使用容器内的，方便使用者选择存放幂等数据的Redis数据源
     */
    @Bean(name = "idempotentStringRedisTemplate")
    @ConditionalOnMissingBean(name = "idempotentStringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean(name = "redisIdempotentRepository")
    @ConditionalOnMissingBean(name = "redisIdempotentRepository")
    public IdempotentRepository redisIdempotentRepository(@NonNull @Qualifier("idempotentStringRedisTemplate") StringRedisTemplate redisTemplate,
                                                          @NonNull IdempotentRedisAdapterProperties properties) {
        return new RedisIdempotentRepositoryImpl(redisTemplate, properties);
    }

}
