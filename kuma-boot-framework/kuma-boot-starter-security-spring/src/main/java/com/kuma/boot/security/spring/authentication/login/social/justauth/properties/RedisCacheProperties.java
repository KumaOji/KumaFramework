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

package com.kuma.boot.security.spring.authentication.login.social.justauth.properties;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 对第三方授权登录用户信息与 token 的持久化（jdbc）数据是否缓存到 redis 的配置
 * @author YongWu zheng
 * @version V1.0  Created by 2020/6/15 19:29
 */
@ConfigurationProperties("ums.cache.redis")
public class RedisCacheProperties {

    private final Cache cache = new Cache();

    /**
     * Redis cache is open, 默认 false
     */
    private Boolean open = false;

    /**
     * 是否使用 spring IOC 容器中的 RedisConnectionFactory， 默认： false. <br>
     * 如果使用 spring IOC 容器中的 RedisConnectionFactory，则要注意 cache.database-index 要与 spring.redis.database 一样。
     */
    private Boolean useIocRedisConnectionFactory = false;

    public static class Cache {

        /**
         * redis cache 存放的 database index, 默认: 0
         */
        private Integer databaseIndex = 0;

        /**
         * 设置缓存管理器管理的缓存的默认过期时间, 默认: 200s
         */
        private Duration defaultExpireTime = Duration.ofSeconds(200);

        /**
         * cache ttl 。使用 0 声明一个永久的缓存。 默认: 180, 单位: 秒<br>
         * 取缓存时间的 20% 作为动态的随机变量上下浮动, 防止同时缓存失效而缓存击穿
         */
        private Duration entryTtl = Duration.ofSeconds(180);

        /**
         * Names of the default caches to consider for caching operations defined
         * in the annotated class.
         */
        private Set<String> cacheNames = new HashSet<>();

        public Integer getDatabaseIndex() {
            return databaseIndex;
        }

        public void setDatabaseIndex(Integer databaseIndex) {
            this.databaseIndex = databaseIndex;
        }

        public Duration getDefaultExpireTime() {
            return defaultExpireTime;
        }

        public void setDefaultExpireTime(Duration defaultExpireTime) {
            this.defaultExpireTime = defaultExpireTime;
        }

        public Duration getEntryTtl() {
            return entryTtl;
        }

        public void setEntryTtl(Duration entryTtl) {
            this.entryTtl = entryTtl;
        }

        public Set<String> getCacheNames() {
            return cacheNames;
        }

        public void setCacheNames(Set<String> cacheNames) {
            this.cacheNames = cacheNames;
        }
    }

    public Cache getCache() {
        return cache;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public Boolean getUseIocRedisConnectionFactory() {
        return useIocRedisConnectionFactory;
    }

    public void setUseIocRedisConnectionFactory(Boolean useIocRedisConnectionFactory) {
        this.useIocRedisConnectionFactory = useIocRedisConnectionFactory;
    }
}
