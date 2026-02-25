/*
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.apache.curator.framework.CuratorFramework
 *  org.redisson.api.RedissonClient
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.ObjectProvider
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.kuma.boot.lock.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.lock.aop.LockAop;
import com.kuma.boot.lock.autoconfigure.properties.LockProperties;
import com.kuma.boot.lock.support.DistributedLock;
import com.kuma.boot.lock.support.local.LocalLock;
import com.kuma.boot.lock.support.redisson.RedissonLock;
import com.kuma.boot.lock.support.zookeeper.CuratorLock;
import org.apache.curator.framework.CuratorFramework;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration
@EnableConfigurationProperties(value={LockProperties.class})
@ConditionalOnProperty(prefix="kuma.boot.lock", name={"enabled"}, havingValue="true")
public class LockAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(LockAutoConfiguration.class, (String)"kuma-boot-starter-lock", (String[])new String[0]);
    }

    @Bean
    public LockAop distributedLockAop(ObjectProvider<DistributedLock> distributedLockProvider) {
        return new LockAop(distributedLockProvider);
    }

    @Configuration
    @ConditionalOnBean(value={CuratorFramework.class})
    @ConditionalOnProperty(prefix="kuma.boot.lock", name={"type"}, havingValue="zookeeper")
    public static class ZookeeperLockAutoConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public DistributedLock curatorDistributedLock(CuratorFramework curatorFramework) {
            return new CuratorLock(curatorFramework);
        }
    }

    @Configuration
    @ConditionalOnBean(value={RedissonClient.class})
    @ConditionalOnProperty(prefix="kuma.boot.lock", name={"type"}, havingValue="redis")
    public static class RedisLockAutoConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public DistributedLock redissonDistributedLock(RedissonClient redissonClient) {
            return new RedissonLock(redissonClient);
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix="kuma.boot.lock", name={"type"}, havingValue="local")
    public static class LocalLockAutoConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public DistributedLock localDistributedLock() {
            return new LocalLock();
        }
    }
}

