/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.l2cache;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.reflect.ClassUtils;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.ibatis.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 扩展的Mybatis二级缓存
 * <p>
 * mybatis-plus:
 * configuration:
 * cache-enabled: true
 * log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
 * </p>
 * {@snippet :
 *  @CacheNamespace(implementation = RedisMybatisCache.class, eviction = RedisMybatisCache.class)
 *  public interface ILogMapper extends BaseSuperMapper<Log>
 *}
 * }
 *
 * @author kuma
 * @version 2023.04
 * @since 2023-04-18 13:21:35
 */
public class RedisMybatisCache implements Cache {

    private static final Logger log = LoggerFactory.getLogger(RedisMybatisCache.class);

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final String id;
    private final AtomicInteger counter = new AtomicInteger(0);
    private RedisRepository redisRepository;
    private static final long EXPIRE_TIME_MINUTES = 30;

    public RedisMybatisCache(final String id) {
        this.id = id;

        ClassUtils.notExistThrow(
                RedisRepository.class,
                "请添加kuma-boot-starter-cache:kuma-boot-starter-cache-redis模块");

        RedisRepository redisRepository = ContextUtils.getBean(RedisRepository.class, true);
        if (Objects.isNull(redisRepository)) {
            throw new BaseException("未查询到");
        }
        this.redisRepository = redisRepository;
    }

    public RedisRepository getRedisTemplate() {
        if (redisRepository == null) {
            synchronized (RedisMybatisCache.class) {
                if (redisRepository == null) {
                    redisRepository = ContextUtils.getBean(RedisRepository.class, true);
                    return redisRepository;
                }
                return redisRepository;
            }
        }
        return redisRepository;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void putObject(Object key, Object value) {
        redisRepository.hset(id, key.toString(), value);
        counter.incrementAndGet();

        log.debug("CACHE - Put data into Mybatis Cache, with key: [{}]", key);
    }

    @Override
    public Object getObject(Object key) {
        Object obj = redisRepository.hget(id, key.toString());
        log.debug("CACHE - Get data from Mybatis Cache, with key: [{}]", key);
        return obj;
    }

    @Override
    public Object removeObject(Object key) {
        Object obj = redisRepository.hDel(id, key.toString());
        counter.decrementAndGet();

        log.debug("CACHE - Remove data from Mybatis Cache, with key: [{}]", key);
        return obj;
    }

    @Override
    public void clear() {
        redisRepository.hDel(id);
        log.debug("CACHE - Clear Mybatis Cache.");
    }

    @Override
    public int getSize() {
        return redisRepository.hsize(id).intValue();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return this.readWriteLock;
    }
}
