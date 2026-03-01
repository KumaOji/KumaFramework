/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.lock.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
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

/**
 * RedisLockAutoConfiguration
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-07 21:17:02
 */
@AutoConfiguration
@EnableConfigurationProperties({LockProperties.class})
@ConditionalOnProperty(prefix = LockProperties.PREFIX, name = "enabled", havingValue = "true")
public class LockAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(LockAutoConfiguration.class, StarterNameConstants.LOCK_STARTER);
    }

    @Configuration
    @ConditionalOnProperty(prefix = LockProperties.PREFIX, name = "type", havingValue = "local")
    public static class LocalLockAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public DistributedLock localDistributedLock() {
            return new LocalLock();
        }
    }

    @Configuration
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnProperty(prefix = LockProperties.PREFIX, name = "type", havingValue = "redis")
    public static class RedisLockAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public DistributedLock redissonDistributedLock(RedissonClient redissonClient) {
            return new RedissonLock(redissonClient);
        }
    }

    @Configuration
    @ConditionalOnBean(CuratorFramework.class)
    @ConditionalOnProperty(prefix = LockProperties.PREFIX, name = "type", havingValue = "zookeeper")
    public static class ZookeeperLockAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public DistributedLock curatorDistributedLock(CuratorFramework curatorFramework) {
            return new CuratorLock(curatorFramework);
        }
    }

    @Bean
    public LockAop distributedLockAop(ObjectProvider<DistributedLock> distributedLockProvider) {
        return new LockAop(distributedLockProvider);
    }
}
