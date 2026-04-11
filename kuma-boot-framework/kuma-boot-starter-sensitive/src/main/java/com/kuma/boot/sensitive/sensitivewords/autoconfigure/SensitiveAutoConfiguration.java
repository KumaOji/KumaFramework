package com.kuma.boot.sensitive.sensitivewords.autoconfigure;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.sensitive.desensitize.DesensitizeProperties;
import com.kuma.boot.sensitive.sensitivewords.SensitiveWordsRunner;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass({RedisRepository.class})
@EnableConfigurationProperties({DesensitizeProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.sensitive",
   name = {"enabled"},
   havingValue = "true"
)
public class SensitiveAutoConfiguration implements InitializingBean {
   public SensitiveAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(SensitiveAutoConfiguration.class, "kuma-boot-starter-sensitive", new String[0]);
   }

   @Bean
   @ConditionalOnBean({RedisRepository.class})
   public SensitiveWordsRunner sensitiveWordsRunner(RedisRepository redisRepository) {
      return new SensitiveWordsRunner(redisRepository);
   }
}
