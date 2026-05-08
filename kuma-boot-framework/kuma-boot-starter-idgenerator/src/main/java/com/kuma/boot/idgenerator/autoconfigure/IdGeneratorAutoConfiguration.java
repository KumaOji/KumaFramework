package com.kuma.boot.idgenerator.autoconfigure;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.idgenerator.autoconfigure.properties.IdGeneratorProperties;
import com.kuma.boot.idgenerator.uid.config.UidGenProperties;
import com.kuma.boot.idgenerator.generator.RedisIdGenerator;
import com.kuma.boot.idgenerator.generator.RedisLockIdGenerator;
import com.kuma.boot.idgenerator.generator.ZookeeperIdGenerator;
import com.kuma.boot.lock.support.DistributedLock;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration
@ConditionalOnProperty(
   prefix = "kuma.boot.idgenerator",
   name = {"enabled"},
   havingValue = "true",
   matchIfMissing = true
)
@EnableConfigurationProperties({IdGeneratorProperties.class, UidGenProperties.class})
public class IdGeneratorAutoConfiguration implements InitializingBean {
   public IdGeneratorAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(IdGeneratorAutoConfiguration.class, "kuma-boot-starter-idgenerator", new String[0]);
   }

   @Configuration
   @ConditionalOnBean({RedisRepository.class})
   @ConditionalOnClass({RedisRepository.class})
   public static class RedisIdGeneratorConfiguration {
      public RedisIdGeneratorConfiguration() {
      }

      @Bean
      @ConditionalOnProperty(
         prefix = "kuma.boot.idgenerator",
         name = {"type"},
         havingValue = "redis",
         matchIfMissing = true
      )
      public RedisIdGenerator idGenerator(RedisRepository redisRepository) {
         return new RedisIdGenerator(redisRepository);
      }
   }

   @Configuration
   @ConditionalOnClass({RedisRepository.class, DistributedLock.class})
   @ConditionalOnBean({RedisRepository.class, DistributedLock.class})
   public static class RedisLockIdGeneratorConfiguration {
      public RedisLockIdGeneratorConfiguration() {
      }

      @Bean
      @ConditionalOnProperty(
         prefix = "kuma.boot.idgenerator",
         name = {"type"},
         havingValue = "redis_lock"
      )
      public RedisLockIdGenerator redisLockIdGenerator(RedisRepository redisRepository, DistributedLock distributedLock) {
         return new RedisLockIdGenerator(redisRepository, distributedLock);
      }
   }

   @Configuration
   @ConditionalOnClass({CuratorFramework.class})
   @ConditionalOnBean({CuratorFramework.class})
   public static class ZookeeperIdGeneratorConfiguration {
      public ZookeeperIdGeneratorConfiguration() {
      }

      @Bean
      @ConditionalOnProperty(
         prefix = "kuma.boot.idgenerator",
         name = {"type"},
         havingValue = "zookeeper"
      )
      public ZookeeperIdGenerator zookeeperIdGenerator(CuratorFramework curatorFramework) {
         return new ZookeeperIdGenerator(curatorFramework);
      }
   }
}
