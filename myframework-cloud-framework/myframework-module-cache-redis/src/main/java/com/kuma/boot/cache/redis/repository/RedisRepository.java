/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.collection.CollUtil
 *  cn.hutool.core.convert.Convert
 *  com.google.common.hash.Funnels
 *  com.google.common.hash.Hashing
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.apache.commons.collections4.CollectionUtils
 *  org.jspecify.annotations.NonNull
 *  org.jspecify.annotations.Nullable
 *  org.springframework.dao.DataAccessException
 *  org.springframework.data.domain.Range
 *  org.springframework.data.redis.connection.BitFieldSubCommands
 *  org.springframework.data.redis.connection.DataType
 *  org.springframework.data.redis.connection.MessageListener
 *  org.springframework.data.redis.connection.RedisClusterNode
 *  org.springframework.data.redis.connection.RedisConnection
 *  org.springframework.data.redis.connection.RedisConnectionFactory
 *  org.springframework.data.redis.connection.RedisServerCommands
 *  org.springframework.data.redis.core.DefaultTypedTuple
 *  org.springframework.data.redis.core.HashOperations
 *  org.springframework.data.redis.core.ListOperations
 *  org.springframework.data.redis.core.RedisCallback
 *  org.springframework.data.redis.core.RedisTemplate
 *  org.springframework.data.redis.core.TimeoutUtils
 *  org.springframework.data.redis.core.ValueOperations
 *  org.springframework.data.redis.core.ZSetOperations$TypedTuple
 *  org.springframework.data.redis.serializer.RedisSerializer
 *  org.springframework.data.redis.serializer.SerializationUtils
 *  org.springframework.util.Assert
 */
package com.kuma.boot.cache.redis.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.google.common.hash.Funnels;
import com.google.common.hash.Hashing;
import com.kuma.boot.cache.redis.model.CacheHashKey;
import com.kuma.boot.cache.redis.model.CacheKey;
import com.kuma.boot.cache.redis.val.NullVal;
import com.kuma.boot.common.utils.log.LogUtils;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.commons.collections4.CollectionUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationUtils;
import org.springframework.util.Assert;

public class RedisRepository {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Map<String, Object> KEY_LOCKS = new ConcurrentHashMap<String, Object>();
    private final RedisTemplate<String, Object> redisTemplate;
    private final boolean defaultCacheNullVal;

    public boolean isClosed() {
        return this.redisTemplate.getConnectionFactory().getConnection().isClosed();
    }

    public RedisRepository(RedisTemplate<String, Object> redisTemplate, boolean cacheNullVal) {
        this.redisTemplate = redisTemplate;
        this.defaultCacheNullVal = cacheNullVal;
    }

    public RedisConnectionFactory getConnectionFactory() {
        return this.redisTemplate.getConnectionFactory();
    }

    public RedisTemplate<String, Object> getRedisTemplate() {
        return this.redisTemplate;
    }

    public void flushDB(RedisClusterNode node) {
        this.redisTemplate.opsForCluster().flushDb(node);
    }

    public void send(String channel, Object data) {
        this.redisTemplate.convertAndSend(channel, data);
    }

    public void setExpire(CacheKey key) {
        if (key != null && key.getExpire() != null) {
            this.redisTemplate.expire((Object)key.getKey(), key.getExpire());
        }
    }

    public static <T> boolean isNullVal(T value) {
        boolean isNull = value == null || NullVal.class.equals(value.getClass());
        return isNull || value.getClass().equals(Object.class) || value instanceof Map && ((Map)value).isEmpty();
    }

    public NullVal newNullVal() {
        return new NullVal();
    }

    public <T> T returnVal(T value) {
        return RedisRepository.isNullVal(value) ? null : (T)value;
    }

    public Long del(CacheKey ... keys) {
        return this.redisTemplate.delete(Arrays.stream(keys).map(CacheKey::getKey).toList());
    }

    public String randomKey() {
        return (String)this.redisTemplate.randomKey();
    }

    public void rename(@NonNull String oldKey, @NonNull String newKey) {
        this.redisTemplate.rename((Object)oldKey, (Object)newKey);
    }

    public Long del(@NonNull Collection<CacheKey> keys) {
        return this.redisTemplate.delete(keys.stream().map(CacheKey::getKey).toList());
    }

    public Boolean renameNx(@NonNull String oldKey, String newKey) {
        return this.redisTemplate.renameIfAbsent((Object)oldKey, (Object)newKey);
    }

    public Boolean move(@NonNull String key, int dbIndex) {
        return this.redisTemplate.move((Object)key, dbIndex);
    }

    public Boolean expire(@NonNull String key, long seconds) {
        return this.redisTemplate.expire((Object)key, seconds, TimeUnit.SECONDS);
    }

    public Boolean expire(@NonNull String key, @NonNull Duration timeout) {
        return this.expire(key, timeout.getSeconds());
    }

    public Boolean expireAt(@NonNull String key, @NonNull Date date) {
        return this.redisTemplate.expireAt((Object)key, date);
    }

    public Boolean pExpire(@NonNull String key, long milliseconds) {
        return this.redisTemplate.expire((Object)key, milliseconds, TimeUnit.MILLISECONDS);
    }

    public Boolean persist(@NonNull String key) {
        return this.redisTemplate.persist((Object)key);
    }

    public String typeCode(@NonNull String key) {
        DataType type = this.redisTemplate.type((Object)key);
        return type == null ? DataType.NONE.code() : type.code();
    }

    public DataType type(@NonNull String key) {
        return this.redisTemplate.type((Object)key);
    }

    public Long ttl(@NonNull String key) {
        return this.redisTemplate.getExpire((Object)key);
    }

    public Long pTtl(@NonNull String key) {
        return this.redisTemplate.getExpire((Object)key, TimeUnit.MILLISECONDS);
    }

    public void set(@NonNull String key, Object value, boolean ... cacheNullValues) {
        boolean cacheNullVal;
        boolean bl = cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        if (!cacheNullVal && value == null) {
            return;
        }
        this.redisTemplate.opsForValue().set((Object)key, value == null ? this.newNullVal() : value);
    }

    public Boolean expireAt(@NonNull String key, long unixTimestamp) {
        return this.expireAt(key, new Date(unixTimestamp));
    }

    public void set(@NonNull CacheKey cacheKey, Object value, boolean ... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        String key = cacheKey.getKey();
        Duration expire = cacheKey.getExpire();
        if (expire == null) {
            this.set(key, value, cacheNullVal);
        } else {
            this.setEx(key, value, expire, cacheNullVal);
        }
    }

    public void setEx(@NonNull String key, Object value, Duration timeout, boolean ... cacheNullValues) {
        boolean cacheNullVal;
        boolean bl = cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        if (!cacheNullVal && value == null) {
            return;
        }
        this.redisTemplate.opsForValue().set((Object)key, value == null ? this.newNullVal() : value, timeout);
    }

    public void setExpire(byte[] key, byte[] value, long time) {
        this.redisTemplate.execute(connection -> {
            connection.stringCommands().setEx(key, time, value);
            LogUtils.info((String)"[redisTemplate redis]\u653e\u5165 \u7f13\u5b58  url:{} ========\u7f13\u5b58\u65f6\u95f4\u4e3a{}\u79d2", (Object[])new Object[]{key, time});
            return 1L;
        });
    }

    public void setEx(@NonNull String key, Object value, long seconds, boolean ... cacheNullValues) {
        this.setEx(key, value, Duration.ofSeconds(seconds), cacheNullValues);
    }

    public @Nullable Boolean setXx(@NonNull String key, String value, boolean ... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        return this.redisTemplate.opsForValue().setIfPresent((Object)key, cacheNullVal && value == null ? this.newNullVal() : value);
    }

    public @Nullable Boolean setXx(@NonNull String key, String value, long seconds, boolean ... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        return this.redisTemplate.opsForValue().setIfPresent((Object)key, cacheNullVal && value == null ? this.newNullVal() : value, seconds, TimeUnit.SECONDS);
    }

    public void setExpire(String key, Object value, long time) {
        this.redisTemplate.opsForValue().set((Object)key, value, time, TimeUnit.SECONDS);
    }

    public <T> @Nullable T lPop(@NonNull String key) {
        return (T)this.redisTemplate.opsForList().leftPop((Object)key);
    }

    public <T> T rPop(@NonNull String key) {
        return (T)this.redisTemplate.opsForList().rightPop((Object)key);
    }

    public <T> T rPoplPush(String sourceKey, String destinationKey) {
        return (T)this.redisTemplate.opsForList().rightPopAndLeftPush((Object)sourceKey, (Object)destinationKey);
    }

    public @Nullable Long lRem(@NonNull String key, long count, Object value) {
        return this.redisTemplate.opsForList().remove((Object)key, count, value);
    }

    public @Nullable Long lLen(@NonNull String key) {
        return this.redisTemplate.opsForList().size((Object)key);
    }

    public <T> @Nullable T lIndex(@NonNull String key, long index) {
        return (T)this.redisTemplate.opsForList().index((Object)key, index);
    }

    public @Nullable Long lInsert(@NonNull String key, Object pivot, Object value) {
        return this.redisTemplate.opsForList().leftPush((Object)key, pivot, value);
    }

    public @Nullable Long rInsert(@NonNull String key, Object pivot, Object value) {
        return this.redisTemplate.opsForList().rightPush((Object)key, pivot, value);
    }

    public void lSet(@NonNull String key, long index, Object value) {
        this.redisTemplate.opsForList().set((Object)key, index, value);
    }

    public @Nullable List<Object> lRange(@NonNull String key, long start, long end) {
        return this.redisTemplate.opsForList().range((Object)key, start, end);
    }

    public void lTrim(@NonNull String key, long start, long end) {
        this.redisTemplate.opsForList().trim((Object)key, start, end);
    }

    public <V> Long sAdd(@NonNull CacheKey key, V ... members) {
        Long count = this.redisTemplate.opsForSet().add((Object)key.getKey(), (Object[])members);
        this.setExpire(key);
        return count;
    }

    public <V> Long sAdd(@NonNull CacheKey key, Collection<V> members) {
        Long count = this.redisTemplate.opsForSet().add((Object)key.getKey(), members.toArray());
        this.setExpire(key);
        return count;
    }

    public Boolean sIsMember(@NonNull CacheKey key, Object member) {
        return this.redisTemplate.opsForSet().isMember((Object)key.getKey(), member);
    }

    public <T> @Nullable T sPop(@NonNull CacheKey key) {
        return (T)this.redisTemplate.opsForSet().pop((Object)key.getKey());
    }

    public <T> @Nullable T sRandMember(@NonNull CacheKey key) {
        return (T)this.redisTemplate.opsForSet().randomMember((Object)key.getKey());
    }

    public <V> @Nullable Set<V> sRandMember(@NonNull CacheKey key, long count) {
        return this.redisTemplate.opsForSet().distinctRandomMembers((Object)key.getKey(), count);
    }

    public <V> @Nullable List<V> sRandMembers(@NonNull CacheKey key, long count) {
        return this.redisTemplate.opsForSet().randomMembers((Object)key.getKey(), count);
    }

    public @Nullable Long sRem(@NonNull CacheKey key, Object ... members) {
        return this.redisTemplate.opsForSet().remove((Object)key.getKey(), members);
    }

    public <V> Boolean sMove(@NonNull CacheKey sourceKey, CacheKey destinationKey, V value) {
        return this.redisTemplate.opsForSet().move((Object)sourceKey.getKey(), value, (Object)destinationKey.getKey());
    }

    public Long sCard(@NonNull CacheKey key) {
        return this.redisTemplate.opsForSet().size((Object)key.getKey());
    }

    public <V> @Nullable Set<V> sMembers(@NonNull CacheKey key) {
        return this.redisTemplate.opsForSet().members((Object)key.getKey());
    }

    public <V> @Nullable Set<V> sInter(@NonNull CacheKey key, @NonNull CacheKey otherKey) {
        return this.redisTemplate.opsForSet().intersect((Object)key.getKey(), (Object)otherKey.getKey());
    }

    public @Nullable Set<Object> sInter(@NonNull CacheKey key, Collection<CacheKey> otherKeys) {
        return this.redisTemplate.opsForSet().intersect((Object)key.getKey(), otherKeys.stream().map(CacheKey::getKey).toList());
    }

    public <V> @Nullable Set<V> sInter(Collection<CacheKey> otherKeys) {
        return this.redisTemplate.opsForSet().intersect(otherKeys.stream().map(CacheKey::getKey).toList());
    }

    public @Nullable Long sInterStore(@NonNull CacheKey key, @NonNull CacheKey otherKey, @NonNull CacheKey destKey) {
        return this.redisTemplate.opsForSet().intersectAndStore((Object)key.getKey(), (Object)otherKey.getKey(), (Object)destKey.getKey());
    }

    public @Nullable Long sInterStore(@NonNull CacheKey key, @NonNull Collection<CacheKey> otherKeys, @NonNull CacheKey destKey) {
        return this.redisTemplate.opsForSet().intersectAndStore((Object)key.getKey(), otherKeys.stream().map(CacheKey::getKey).toList(), (Object)destKey.getKey());
    }

    public @Nullable Long sInterStore(Collection<CacheKey> otherKeys, @NonNull CacheKey destKey) {
        return this.redisTemplate.opsForSet().intersectAndStore(otherKeys.stream().map(CacheKey::getKey).toList(), (Object)destKey.getKey());
    }

    public <V> @Nullable Set<V> sUnion(@NonNull CacheKey key, @NonNull CacheKey otherKey) {
        return this.redisTemplate.opsForSet().union((Object)key.getKey(), (Object)otherKey.getKey());
    }

    public <V> @Nullable Set<V> sUnion(@NonNull CacheKey key, Collection<CacheKey> otherKeys) {
        return this.redisTemplate.opsForSet().union((Object)key.getKey(), otherKeys.stream().map(CacheKey::getKey).toList());
    }

    public <V> @Nullable Set<V> sUnion(Collection<CacheKey> otherKeys) {
        return this.redisTemplate.opsForSet().union(otherKeys.stream().map(CacheKey::getKey).toList());
    }

    public Long sUnionStore(@NonNull CacheKey key, @NonNull CacheKey otherKey, @NonNull CacheKey distKey) {
        return this.redisTemplate.opsForSet().unionAndStore((Object)key.getKey(), (Object)otherKey.getKey(), (Object)distKey.getKey());
    }

    public Long sUnionStore(Collection<CacheKey> otherKeys, @NonNull CacheKey distKey) {
        return this.redisTemplate.opsForSet().unionAndStore(otherKeys.stream().map(CacheKey::getKey).toList(), (Object)distKey.getKey());
    }

    public <V> @Nullable Set<V> sDiff(@NonNull CacheKey key, @NonNull CacheKey otherKey) {
        return this.redisTemplate.opsForSet().difference((Object)key.getKey(), (Object)otherKey.getKey());
    }

    public <V> Set<V> sDiff(Collection<CacheKey> otherKeys) {
        return this.redisTemplate.opsForSet().difference(otherKeys.stream().map(CacheKey::getKey).toList());
    }

    public Long sDiffStore(@NonNull CacheKey key, @NonNull CacheKey otherKey, @NonNull CacheKey distKey) {
        return this.redisTemplate.opsForSet().differenceAndStore((Object)key.getKey(), (Object)otherKey.getKey(), (Object)distKey.getKey());
    }

    public Long sDiffStore(Collection<CacheKey> otherKeys, @NonNull CacheKey distKey) {
        return this.redisTemplate.opsForSet().differenceAndStore(otherKeys.stream().map(CacheKey::getKey).toList(), (Object)distKey.getKey());
    }

    public Boolean zAdd(@NonNull String key, Object member, double score) {
        return this.redisTemplate.opsForZSet().add((Object)key, member, score);
    }

    public Long zAdd(@NonNull String key, Map<Object, Double> scoreMembers) {
        HashSet tuples = new HashSet();
        scoreMembers.forEach((score, member) -> tuples.add(new DefaultTypedTuple(score, member)));
        return this.redisTemplate.opsForSet().add((Object)key, new Object[]{tuples});
    }

    public Double zScore(@NonNull String key, Object member) {
        return this.redisTemplate.opsForZSet().score((Object)key, member);
    }

    public Double zIncrBy(@NonNull String key, Object member, double score) {
        return this.redisTemplate.opsForZSet().incrementScore((Object)key, member, score);
    }

    public Long zCard(@NonNull String key) {
        return this.redisTemplate.opsForZSet().zCard((Object)key);
    }

    public Long zCount(@NonNull String key, double min, double max) {
        return this.redisTemplate.opsForZSet().count((Object)key, min, max);
    }

    public @Nullable Set<Object> zRange(@NonNull String key, long start, long end) {
        return this.redisTemplate.opsForZSet().range((Object)key, start, end);
    }

    public @Nullable Set<// Could not load outer class - annotation placement on inner may be incorrect
    ZSetOperations.TypedTuple<Object>> zRangeWithScores(@NonNull String key, long start, long end) {
        return this.redisTemplate.opsForZSet().rangeWithScores((Object)key, start, end);
    }

    public @Nullable Set<Object> zRevrange(@NonNull String key, long start, long end) {
        return this.redisTemplate.opsForZSet().reverseRange((Object)key, start, end);
    }

    public @Nullable Set<// Could not load outer class - annotation placement on inner may be incorrect
    ZSetOperations.TypedTuple<Object>> zRevrangeWithScores(@NonNull String key, long start, long end) {
        return this.redisTemplate.opsForZSet().reverseRangeWithScores((Object)key, start, end);
    }

    public Set<Object> zRangeByScore(@NonNull String key, double min, double max) {
        return this.redisTemplate.opsForZSet().rangeByScore((Object)key, min, max);
    }

    public Set<ZSetOperations.TypedTuple<Object>> zRangeByScoreWithScores(@NonNull String key, double min, double max) {
        return this.redisTemplate.opsForZSet().rangeByScoreWithScores((Object)key, min, max);
    }

    public Set<Object> zReverseRange(@NonNull String key, double min, double max) {
        return this.redisTemplate.opsForZSet().reverseRangeByScore((Object)key, min, max);
    }

    public Set<ZSetOperations.TypedTuple<Object>> zReverseRangeByScoreWithScores(@NonNull String key, double min, double max) {
        return this.redisTemplate.opsForZSet().reverseRangeByScoreWithScores((Object)key, min, max);
    }

    public @Nullable Long zRank(@NonNull String key, Object member) {
        return this.redisTemplate.opsForZSet().rank((Object)key, member);
    }

    public Long zRevrank(@NonNull String key, Object member) {
        return this.redisTemplate.opsForZSet().reverseRank((Object)key, member);
    }

    public Long zRem(@NonNull String key, Object ... members) {
        return this.redisTemplate.opsForSet().remove((Object)key, members);
    }

    public Long zRem(@NonNull String key, long start, long end) {
        return this.redisTemplate.opsForZSet().removeRange((Object)key, start, end);
    }

    public Long zRemRangeByScore(@NonNull String key, double min, double max) {
        return this.redisTemplate.opsForZSet().removeRangeByScore((Object)key, min, max);
    }

    public void setExpire(String[] keys, Object[] values, long time) {
        for (int i = 0; i < keys.length; ++i) {
            this.redisTemplate.opsForValue().set((Object)keys[i], values[i], time, TimeUnit.SECONDS);
        }
    }

    public Long getCounter(@NonNull CacheKey key, Long ... defaultValue) {
        Object val = this.redisTemplate.opsForValue().get((Object)key.getKey());
        if (RedisRepository.isNullVal(val)) {
            return defaultValue.length > 0 ? defaultValue[0] : null;
        }
        return Convert.toLong((Object)val);
    }

    public Long getCounter(@NonNull CacheKey key, Function<CacheKey, Long> loader) {
        Object val = this.redisTemplate.opsForValue().get((Object)key.getKey());
        if (RedisRepository.isNullVal(val)) {
            return loader.apply(key);
        }
        return Convert.toLong((Object)val);
    }

    public Long decr(@NonNull CacheKey key) {
        Long decr = this.redisTemplate.opsForValue().decrement((Object)key.getKey());
        this.setExpire(key);
        return decr;
    }

    public Long decrBy(@NonNull CacheKey key, long decrement) {
        Long decr = this.redisTemplate.opsForValue().decrement((Object)key.getKey(), decrement);
        this.setExpire(key);
        return decr;
    }

    public void hSet(@NonNull String key, @NonNull Object field, Object value, boolean ... cacheNullValues) {
        boolean cacheNullVal;
        boolean bl = cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        if (!cacheNullVal && value == null) {
            return;
        }
        this.redisTemplate.opsForHash().put((Object)key, field, value == null ? this.newNullVal() : value);
    }

    public void hSet(@NonNull CacheHashKey key, Object value, boolean ... cacheNullValues) {
        this.hSet(key.getKey(), key.getField(), value, cacheNullValues);
        this.setExpire(key);
    }

    public <T> @Nullable T hGet(@NonNull String key, @NonNull Object field, boolean ... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        Object value = this.redisTemplate.opsForHash().get((Object)key, field);
        if (value == null && cacheNullVal) {
            this.hSet(key, field, this.newNullVal(), true);
        }
        return (T)this.returnVal(value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> @Nullable T hGet(@NonNull String key, @NonNull Object field, BiFunction<String, Object, T> loader, boolean ... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        Object value = this.redisTemplate.opsForHash().get((Object)key, field);
        if (value != null) {
            return (T)this.returnVal(value);
        }
        String lockKey = key + "@" + String.valueOf(field);
        Object object = KEY_LOCKS.computeIfAbsent(lockKey, v -> new Object());
        synchronized (object) {
            value = this.redisTemplate.opsForHash().get((Object)key, field);
            if (value != null) {
                return (T)this.returnVal(value);
            }
            try {
                value = loader.apply(key, field);
                this.hSet(key, field, value, cacheNullVal);
            }
            finally {
                KEY_LOCKS.remove(lockKey);
            }
        }
        return (T)this.returnVal(value);
    }

    public <T> @Nullable T hGet(@NonNull CacheHashKey key, boolean ... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        Object value = this.redisTemplate.opsForHash().get((Object)key.getKey(), key.getField());
        if (value == null && cacheNullVal) {
            this.hSet(key, (Object)this.newNullVal(), true);
        }
        return (T)this.returnVal(value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> @Nullable T hGet(@NonNull CacheHashKey key, Function<CacheHashKey, T> loader, boolean ... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        Object value = this.redisTemplate.opsForHash().get((Object)key.getKey(), key.getField());
        if (value != null) {
            return (T)this.returnVal(value);
        }
        String lockKey = key.getKey() + "@" + String.valueOf(key.getField());
        Object object = KEY_LOCKS.computeIfAbsent(lockKey, v -> new Object());
        synchronized (object) {
            value = this.redisTemplate.opsForHash().get((Object)key.getKey(), key.getField());
            if (value != null) {
                return (T)this.returnVal(value);
            }
            try {
                value = loader.apply(key);
                this.hSet(key, value, cacheNullVal);
            }
            finally {
                KEY_LOCKS.remove(key.getKey());
            }
        }
        return (T)this.returnVal(value);
    }

    public Boolean hExists(@NonNull String key, @NonNull Object field) {
        return this.redisTemplate.opsForHash().hasKey((Object)key, field);
    }

    public Boolean hExists(@NonNull CacheHashKey cacheHashKey) {
        return this.redisTemplate.opsForHash().hasKey((Object)cacheHashKey.getKey(), cacheHashKey.getField());
    }

    public Long hDel(@NonNull String key, Object ... fields) {
        return this.redisTemplate.opsForHash().delete((Object)key, fields);
    }

    public Long hDel(@NonNull CacheHashKey key) {
        return this.redisTemplate.opsForHash().delete((Object)key.getKey(), new Object[]{key.getField()});
    }

    public Long hLen(@NonNull String key) {
        return this.redisTemplate.opsForHash().size((Object)key);
    }

    public Long hStrLen(@NonNull String key, @NonNull Object field) {
        return this.redisTemplate.opsForHash().lengthOfValue((Object)key, field);
    }

    public Long hIncrBy(@NonNull CacheHashKey key, long increment) {
        Long hIncrBy = this.redisTemplate.opsForHash().increment((Object)key.getKey(), key.getField(), increment);
        if (key.getExpire() != null) {
            this.redisTemplate.expire((Object)key.getKey(), key.getExpire());
        }
        return hIncrBy;
    }

    public Double hIncrByFloat(@NonNull CacheHashKey key, double increment) {
        Double hIncrBy = this.redisTemplate.opsForHash().increment((Object)key.getKey(), key.getField(), increment);
        if (key.getExpire() != null) {
            this.redisTemplate.expire((Object)key.getKey(), key.getExpire());
        }
        return hIncrBy;
    }

    public <K, V> void hmSet(@NonNull String key, @NonNull Map<K, V> hash, boolean ... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        HashMap newMap = new HashMap(hash.size());
        hash.forEach((k, v) -> {
            if (v == null && cacheNullVal) {
                newMap.put(k, this.newNullVal());
            } else {
                newMap.put(k, v);
            }
        });
        this.redisTemplate.opsForHash().putAll((Object)key, newMap);
    }

    public List<Object> hmGet(@NonNull String key, Object ... fields) {
        return this.hmGet(key, Arrays.asList(fields));
    }

    public List<Object> hmGet(@NonNull String key, @NonNull Collection<Object> fields) {
        List list = this.redisTemplate.opsForHash().multiGet((Object)key, fields);
        return list.stream().map(this::returnVal).toList();
    }

    public <HK> Set<HK> hKeys(@NonNull String key) {
        return this.redisTemplate.opsForHash().keys((Object)key);
    }

    public <HV> List<HV> hVals(@NonNull String key) {
        return this.redisTemplate.opsForHash().values((Object)key);
    }

    public <K, V> Map<K, V> hGetAll(@NonNull String key) {
        Map map = this.redisTemplate.opsForHash().entries((Object)key);
        return this.returnMapVal(map);
    }

    public <K, V> Map<K, V> hGetAll(@NonNull CacheHashKey key) {
        Map map = this.redisTemplate.opsForHash().entries((Object)key.getKey());
        return this.returnMapVal(map);
    }

    private <K, V> Map<K, V> returnMapVal(Map<K, V> map) {
        HashMap newMap = new HashMap(map.size());
        if (CollUtil.isNotEmpty(map)) {
            map.forEach((k, v) -> {
                if (!RedisRepository.isNullVal(v)) {
                    newMap.put(k, v);
                }
            });
        }
        return newMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <K, V> @Nullable Map<K, V> hGetAll(@NonNull CacheHashKey key, Function<CacheHashKey, Map<K, V>> loader, boolean ... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        Map<K, V> map = this.redisTemplate.opsForHash().entries((Object)key.getKey());
        if (CollUtil.isNotEmpty((Map)map)) {
            return this.returnMapVal(map);
        }
        String lockKey = key.getKey();
        Object object = KEY_LOCKS.computeIfAbsent(lockKey, v -> new Object());
        synchronized (object) {
            map = this.redisTemplate.opsForHash().entries((Object)key.getKey());
            if (CollUtil.isNotEmpty((Map)map)) {
                return this.returnMapVal(map);
            }
            try {
                map = loader.apply(key);
                this.hmSet(key.getKey(), map, cacheNullVal);
            }
            finally {
                KEY_LOCKS.remove(key.getKey());
            }
        }
        return this.returnMapVal(map);
    }

    public @Nullable Long lPush(@NonNull String key, Object ... values) {
        return this.redisTemplate.opsForList().leftPushAll((Object)key, values);
    }

    public @Nullable Long lPush(@NonNull String key, Collection<Object> values) {
        return this.redisTemplate.opsForList().leftPushAll((Object)key, values);
    }

    public @Nullable Long lPushX(@NonNull String key, Object values) {
        return this.redisTemplate.opsForList().leftPushIfPresent((Object)key, values);
    }

    public @Nullable Long rPush(@NonNull String key, Object ... values) {
        return this.redisTemplate.opsForList().rightPushAll((Object)key, values);
    }

    public @Nullable Long rPush(@NonNull String key, Collection<Object> values) {
        return this.redisTemplate.opsForList().rightPushAll((Object)key, values);
    }

    public @Nullable Long rPushX(@NonNull String key, Object value) {
        return this.redisTemplate.opsForList().rightPushIfPresent((Object)key, value);
    }

    public @Nullable Boolean setXx(@NonNull String key, String value, Duration timeout, boolean ... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        return this.redisTemplate.opsForValue().setIfPresent((Object)key, cacheNullVal && value == null ? this.newNullVal() : value, timeout);
    }

    public @Nullable Boolean setNx(@NonNull String key, String value, boolean ... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        return this.redisTemplate.opsForValue().setIfAbsent((Object)key, cacheNullVal && value == null ? this.newNullVal() : value);
    }

    public <T> @Nullable T get(@NonNull String key, boolean ... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        Object value = this.redisTemplate.opsForValue().get((Object)key);
        if (value == null && cacheNullVal) {
            this.set(key, (Object)this.newNullVal(), true);
        }
        return (T)this.returnVal(value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> @Nullable T get(@NonNull String key, Function<String, T> loader, boolean ... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        Object value = this.redisTemplate.opsForValue().get((Object)key);
        if (value != null) {
            return (T)this.returnVal(value);
        }
        Object object = KEY_LOCKS.computeIfAbsent(key, v -> new Object());
        synchronized (object) {
            value = this.redisTemplate.opsForValue().get((Object)key);
            if (value != null) {
                return (T)this.returnVal(value);
            }
            try {
                value = loader.apply(key);
                this.set(key, value, cacheNullVal);
            }
            finally {
                KEY_LOCKS.remove(key);
            }
        }
        return (T)this.returnVal(value);
    }

    public <T> T getSet(@NonNull String key, Object value) {
        Object val = this.redisTemplate.opsForValue().getAndSet((Object)key, value == null ? this.newNullVal() : value);
        return (T)this.returnVal(val);
    }

    public <T> @Nullable T get(@NonNull CacheKey key, boolean ... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        Object value = this.redisTemplate.opsForValue().get((Object)key.getKey());
        if (value == null && cacheNullVal) {
            this.set(key, (Object)this.newNullVal(), true);
        }
        return (T)this.returnVal(value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> @Nullable T get(@NonNull CacheKey key, Function<CacheKey, T> loader, boolean ... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        Object value = this.redisTemplate.opsForValue().get((Object)key.getKey());
        if (value != null) {
            return (T)this.returnVal(value);
        }
        Object object = KEY_LOCKS.computeIfAbsent(key.getKey(), v -> new Object());
        synchronized (object) {
            value = this.redisTemplate.opsForValue().get((Object)key.getKey());
            if (value != null) {
                return (T)this.returnVal(value);
            }
            try {
                value = loader.apply(key);
                this.set(key, value, cacheNullVal);
            }
            finally {
                KEY_LOCKS.remove(key.getKey());
            }
        }
        return (T)this.returnVal(value);
    }

    public @Nullable Long strLen(@NonNull String key) {
        return this.redisTemplate.opsForValue().size((Object)key);
    }

    public @Nullable Integer append(@NonNull String key, String value) {
        return this.redisTemplate.opsForValue().append((Object)key, value);
    }

    public void setRange(@NonNull String key, String value, long offset) {
        this.redisTemplate.opsForValue().set((Object)key, (Object)value, offset);
    }

    public String getRange(@NonNull String key, long start, long end) {
        return this.redisTemplate.opsForValue().get((Object)key, start, end);
    }

    private Map<String, Object> mSetMap(@NonNull Map<String, Object> map, boolean cacheNullVal) {
        HashMap<String, Object> mSetMap = new HashMap<String, Object>(map.size());
        map.forEach((k, v) -> {
            if (v == null && cacheNullVal) {
                mSetMap.put((String)k, this.newNullVal());
            } else {
                mSetMap.put((String)k, v);
            }
        });
        return mSetMap;
    }

    public void mSet(@NonNull Map<String, Object> map, boolean cacheNullVal) {
        Map<String, Object> mSetMap = this.mSetMap(map, cacheNullVal);
        this.redisTemplate.opsForValue().multiSet(mSetMap);
    }

    public void mSet(@NonNull Map<String, Object> map) {
        this.mSet(map, this.defaultCacheNullVal);
    }

    public void mSetNx(@NonNull Map<String, Object> map, boolean cacheNullVal) {
        Map<String, Object> mSetMap = this.mSetMap(map, cacheNullVal);
        this.redisTemplate.opsForValue().multiSetIfAbsent(mSetMap);
    }

    public void mSetNx(@NonNull Map<String, Object> map) {
        this.mSetNx(map, this.defaultCacheNullVal);
    }

    public <T> List<T> mGet(String ... keys) {
        return this.mGet(Arrays.asList(keys));
    }

    public <T> List<T> mGet(CacheKey ... keys) {
        return this.mGetByCacheKey(Arrays.asList(keys));
    }

    public <T> List<T> mGet(@NonNull Collection<String> keys) {
        List list = this.redisTemplate.opsForValue().multiGet(keys);
        return list == null ? Collections.emptyList() : list.stream().map(this::returnVal).toList();
    }

    public <T> List<T> mGetByCacheKey(@NonNull Collection<CacheKey> cacheKeys) {
        List<String> keys = cacheKeys.stream().map(CacheKey::getKey).toList();
        List list = this.redisTemplate.opsForValue().multiGet(keys);
        return list == null ? Collections.emptyList() : list.stream().map(this::returnVal).toList();
    }

    public Long incr(@NonNull CacheKey key) {
        Long increment = this.redisTemplate.opsForValue().increment((Object)key.getKey());
        this.setExpire(key);
        return increment;
    }

    public Long incrBy(@NonNull CacheKey key, long increment) {
        Long incrBy = this.redisTemplate.opsForValue().increment((Object)key.getKey(), increment);
        this.setExpire(key);
        return incrBy;
    }

    public Double incrByFloat(@NonNull CacheKey key, double increment) {
        Double incrByFloat = this.redisTemplate.opsForValue().increment((Object)key.getKey(), increment);
        this.setExpire(key);
        return incrByFloat;
    }

    public void set(String[] keys, Object[] values) {
        this.redisTemplate.execute(connection -> {
            RedisSerializer<String> serializer = this.getRedisSerializer();
            for (int i = 0; i < keys.length; ++i) {
                this.set(keys[i], values[i]);
            }
            return 1L;
        });
    }

    public void set(String key, Object value) {
        this.redisTemplate.opsForValue().set((Object)key, value);
    }

    public List<String> willExpire(String key, long time) {
        ArrayList<String> keysList = new ArrayList<String>();
        this.redisTemplate.execute(connection -> {
            Set keys = this.redisTemplate.keys((Object)(key + "*"));
            assert (keys != null);
            for (String key1 : keys) {
                Long ttl = connection.keyCommands().ttl(key1.getBytes(DEFAULT_CHARSET));
                if (0L > ttl || ttl > 2L * time) continue;
                keysList.add(key1);
            }
            return keysList;
        });
        return keysList;
    }

    public Set<String> keys(String keyPatten) {
        return (Set)this.redisTemplate.execute(connection -> this.redisTemplate.keys((Object)(keyPatten + "*")));
    }

    public byte[] get(byte[] key) {
        byte[] result = (byte[])this.redisTemplate.execute(connection -> connection.stringCommands().get(key));
        LogUtils.info((String)"[redisTemplate redis]\u53d6\u51fa \u7f13\u5b58  url:{} ", (Object[])new Object[]{key});
        return result;
    }

    public Object get(String key) {
        return this.redisTemplate.opsForValue().get((Object)key);
    }

    public Map<String, Object> getKeysValues(String keyPatten) {
        LogUtils.info((String)"[redisTemplate redis]  getValues()  patten={} ", (Object[])new Object[]{keyPatten});
        return (Map)this.redisTemplate.execute(connection -> {
            RedisSerializer<String> serializer = this.getRedisSerializer();
            HashMap<String, Object> maps = new HashMap<String, Object>(16);
            Set keys = this.redisTemplate.keys((Object)(keyPatten + "*"));
            if (CollectionUtils.isNotEmpty((Collection)keys)) {
                for (String key : keys) {
                    maps.put(key, this.get(key));
                }
            }
            return maps;
        });
    }

    public HashOperations<String, String, Object> opsForHash() {
        return this.redisTemplate.opsForHash();
    }

    public ValueOperations<String, Object> opsForValue() {
        return this.redisTemplate.opsForValue();
    }

    public void putHashValue(String key, String hashKey, Object hashValue) {
        LogUtils.info((String)"[redisTemplate redis]  putHashValue()  key={},hashKey={},hashValue={} ", (Object[])new Object[]{key, hashKey, hashValue});
        this.opsForHash().put((Object)key, (Object)hashKey, hashValue);
    }

    public Object getHashValues(String key, String hashKey) {
        LogUtils.info((String)"[redisTemplate redis]  getHashValues()  key={},hashKey={}", (Object[])new Object[]{key, hashKey});
        return this.opsForHash().get((Object)key, (Object)hashKey);
    }

    public void delHashValues(String key, Object ... hashKeys) {
        LogUtils.info((String)"[redisTemplate redis]  delHashValues()  key={}", (Object[])new Object[]{key});
        this.opsForHash().delete((Object)key, hashKeys);
    }

    public Map<String, Object> getHashValue(String key) {
        LogUtils.info((String)"[redisTemplate redis]  getHashValue()  key={}", (Object[])new Object[]{key});
        return this.opsForHash().entries((Object)key);
    }

    public void putHashValues(String key, Map<String, Object> map) {
        this.opsForHash().putAll((Object)key, map);
    }

    public long dbSize() {
        return (Long)this.redisTemplate.execute(RedisServerCommands::dbSize);
    }

    public String flushDB() {
        return (String)this.redisTemplate.execute(connection -> {
            connection.serverCommands().flushDb();
            return "ok";
        });
    }

    public Boolean exists(String key) {
        return (Boolean)this.redisTemplate.execute(connection -> connection.keyCommands().exists(key.getBytes(DEFAULT_CHARSET)));
    }

    public Long del(String ... keys) {
        return (Long)this.redisTemplate.execute(connection -> {
            Long result = 0L;
            for (String key : keys) {
                result = connection.keyCommands().del((byte[][])new byte[][]{key.getBytes(DEFAULT_CHARSET)});
            }
            return result;
        });
    }

    protected RedisSerializer<String> getRedisSerializer() {
        return this.redisTemplate.getStringSerializer();
    }

    public long incr(String key) {
        return (Long)this.redisTemplate.execute(connection -> {
            RedisSerializer<String> redisSerializer = this.getRedisSerializer();
            return connection.stringCommands().incr(redisSerializer.serialize((Object)key));
        });
    }

    public ListOperations<String, Object> opsForList() {
        return this.redisTemplate.opsForList();
    }

    public Long leftPush(String key, Object value) {
        return this.opsForList().leftPush((Object)key, value);
    }

    public Object leftPop(String key) {
        return this.opsForList().leftPop((Object)key);
    }

    public Long in(String key, Object value) {
        return this.opsForList().rightPush((Object)key, value);
    }

    public Object rightPop(String key) {
        return this.opsForList().rightPop((Object)key);
    }

    public Long length(String key) {
        return this.opsForList().size((Object)key);
    }

    public void remove(String key, long i, Object value) {
        this.opsForList().remove((Object)key, i, value);
    }

    public void set(String key, long index, Object value) {
        this.opsForList().set((Object)key, index, value);
    }

    public List<Object> getList(String key, int start, int end) {
        return this.opsForList().range((Object)key, (long)start, (long)end);
    }

    public Long leftPushAll(String key, List<String> list) {
        return this.opsForList().leftPushAll((Object)key, new Object[]{list});
    }

    public void insert(String key, long index, Object value) {
        this.opsForList().set((Object)key, index, value);
    }

    public Boolean expire(String key, Long time) {
        try {
            if (time > 0L) {
                this.redisTemplate.expire((Object)key, time.longValue(), TimeUnit.SECONDS);
            }
            return true;
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return false;
        }
    }

    public void setExpire(String key, Object value, long time, TimeUnit timeUnit) {
        this.redisTemplate.opsForValue().set((Object)key, value, time, timeUnit);
    }

    public void setExpire(String key, Object value, final long time, final TimeUnit timeUnit, RedisSerializer<Object> valueSerializer) {
        final byte[] rawKey = this.rawKey(key);
        final byte[] rawValue = this.rawValue(value, valueSerializer);
        this.redisTemplate.execute((RedisCallback)new RedisCallback<Object>(){
            {
                Objects.requireNonNull(this$0);
            }

            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                this.potentiallyUsePsetEx(connection);
                return null;
            }

            public void potentiallyUsePsetEx(RedisConnection connection) {
                if (!TimeUnit.MILLISECONDS.equals((Object)timeUnit) || !this.failsafeInvokePsetEx(connection)) {
                    connection.stringCommands().setEx(rawKey, TimeoutUtils.toSeconds((long)time, (TimeUnit)timeUnit), rawValue);
                }
            }

            private boolean failsafeInvokePsetEx(RedisConnection connection) {
                boolean failed = false;
                try {
                    connection.stringCommands().pSetEx(rawKey, time, rawValue);
                }
                catch (UnsupportedOperationException e) {
                    failed = true;
                }
                return !failed;
            }
        }, true);
    }

    public Long getExpire(String key) {
        return this.redisTemplate.getExpire((Object)key, TimeUnit.SECONDS);
    }

    public Boolean hasKey(String key) {
        try {
            return this.redisTemplate.hasKey((Object)key);
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return false;
        }
    }

    public Object get(String key, RedisSerializer<Object> valueSerializer) {
        byte[] rawKey = this.rawKey(key);
        return this.redisTemplate.execute(connection -> this.deserializeValue(connection.stringCommands().get(rawKey), valueSerializer), true);
    }

    public Boolean set(String key, Object value, Long time) {
        try {
            if (time > 0L) {
                this.redisTemplate.opsForValue().set((Object)key, value, time.longValue(), TimeUnit.SECONDS);
            } else {
                this.set(key, value);
            }
            return true;
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return false;
        }
    }

    public Boolean set(String key, Object value, Duration timeout) {
        try {
            Assert.notNull((Object)timeout, (String)"Timeout must not be null!");
            if (TimeoutUtils.hasMillis((Duration)timeout)) {
                this.redisTemplate.opsForValue().set((Object)key, value, timeout.toMillis(), TimeUnit.MILLISECONDS);
            } else {
                this.redisTemplate.opsForValue().set((Object)key, value, timeout.getSeconds(), TimeUnit.SECONDS);
            }
            return true;
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return false;
        }
    }

    public Long incr(String key, Long delta) {
        if (delta < 0L) {
            throw new RuntimeException("\u9012\u589e\u56e0\u5b50\u5fc5\u987b\u5927\u4e8e0");
        }
        return this.redisTemplate.opsForValue().increment((Object)key, delta.longValue());
    }

    public Long decr(String key, Long delta) {
        if (delta < 0L) {
            throw new RuntimeException("\u9012\u51cf\u56e0\u5b50\u5fc5\u987b\u5927\u4e8e0");
        }
        return this.redisTemplate.opsForValue().increment((Object)key, -delta.longValue());
    }

    public Object hget(String key, String item) {
        return this.redisTemplate.opsForHash().get((Object)key, (Object)item);
    }

    public Map<Object, Object> hmget(String key) {
        return this.redisTemplate.opsForHash().entries((Object)key);
    }

    public Boolean hmset(String key, Map<String, Object> map) {
        try {
            this.redisTemplate.opsForHash().putAll((Object)key, map);
            return true;
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return false;
        }
    }

    public Boolean hmset(String key, Map<String, Object> map, Long time) {
        try {
            this.redisTemplate.opsForHash().putAll((Object)key, map);
            if (time > 0L) {
                this.expire(key, time);
            }
            return true;
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return false;
        }
    }

    public Boolean hset(String key, String item, Object value) {
        try {
            this.redisTemplate.opsForHash().put((Object)key, (Object)item, value);
            return true;
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return false;
        }
    }

    public Boolean hset(String key, String item, Object value, Long time) {
        try {
            this.redisTemplate.opsForHash().put((Object)key, (Object)item, value);
            if (time > 0L) {
                this.expire(key, time);
            }
            return true;
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return false;
        }
    }

    public void hdel(String key, Object ... item) {
        this.redisTemplate.opsForHash().delete((Object)key, item);
    }

    public Boolean hHasKey(String key, String item) {
        return this.redisTemplate.opsForHash().hasKey((Object)key, (Object)item);
    }

    public Double hincr(String key, String item, Double by) {
        return this.redisTemplate.opsForHash().increment((Object)key, (Object)item, by.doubleValue());
    }

    public Double hdecr(String key, String item, Double by) {
        return this.redisTemplate.opsForHash().increment((Object)key, (Object)item, -by.doubleValue());
    }

    public Set<Object> sGet(String key) {
        try {
            return this.redisTemplate.opsForSet().members((Object)key);
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return null;
        }
    }

    public Boolean sHasKey(String key, Object value) {
        try {
            return this.redisTemplate.opsForSet().isMember((Object)key, value);
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return false;
        }
    }

    public Long sSet(String key, Object ... values) {
        try {
            return this.redisTemplate.opsForSet().add((Object)key, values);
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return 0L;
        }
    }

    public Long sSetAndTime(String key, Long time, Object ... values) {
        try {
            Long count = this.redisTemplate.opsForSet().add((Object)key, values);
            if (time > 0L) {
                this.expire(key, time);
            }
            return count;
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return 0L;
        }
    }

    public Long sGetSetSize(String key) {
        try {
            return this.redisTemplate.opsForSet().size((Object)key);
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return 0L;
        }
    }

    public Long setRemove(String key, Object ... values) {
        try {
            return this.redisTemplate.opsForSet().remove((Object)key, values);
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return 0L;
        }
    }

    public <T> List<T> lGet(String key, Long start, Long end) {
        try {
            return this.redisTemplate.opsForList().range((Object)key, start.longValue(), end.longValue());
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return null;
        }
    }

    public Long lGetListSize(String key) {
        try {
            return this.redisTemplate.opsForList().size((Object)key);
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return 0L;
        }
    }

    public <V> V lGetIndex(String key, Long index) {
        try {
            return (V)this.redisTemplate.opsForList().index((Object)key, index.longValue());
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return null;
        }
    }

    public Boolean lSet(String key, Object value, Long time) {
        try {
            this.redisTemplate.opsForList().rightPush((Object)key, value);
            if (time > 0L) {
                this.expire(key, time);
            }
            return true;
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return false;
        }
    }

    public Boolean lSet(String key, List<?> value) {
        try {
            this.redisTemplate.opsForList().rightPushAll((Object)key, new Object[]{value});
            return true;
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return false;
        }
    }

    public Boolean lSet(String key, List<?> value, Long time) {
        try {
            this.redisTemplate.opsForList().rightPushAll((Object)key, new Object[]{value});
            if (time > 0L) {
                this.expire(key, time);
            }
            return true;
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return false;
        }
    }

    public Boolean lUpdateIndex(String key, Long index, Object value) {
        try {
            this.redisTemplate.opsForList().set((Object)key, index.longValue(), value);
            return true;
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return false;
        }
    }

    public Long lRemove(String key, Long count, Object value) {
        try {
            return this.redisTemplate.opsForList().remove((Object)key, count.longValue(), value);
        }
        catch (Exception e) {
            LogUtils.error((String)"Exception: {}", (Object[])new Object[]{e.getMessage()});
            return 0L;
        }
    }

    public List<Object> getList(String key, int start, int end, RedisSerializer<Object> valueSerializer) {
        byte[] rawKey = this.rawKey(key);
        return (List)this.redisTemplate.execute(connection -> this.deserializeValues(connection.listCommands().lRange(rawKey, (long)start, (long)end), valueSerializer), true);
    }

    private byte[] rawKey(Object key) {
        Assert.notNull((Object)key, (String)"non null key required");
        if (key instanceof byte[]) {
            return (byte[])key;
        }
        RedisSerializer redisSerializer = this.redisTemplate.getKeySerializer();
        return redisSerializer.serialize(key);
    }

    private byte[] rawValue(Object value, RedisSerializer valueSerializer) {
        if (value instanceof byte[]) {
            return (byte[])value;
        }
        return valueSerializer.serialize(value);
    }

    private List deserializeValues(List<byte[]> rawValues, RedisSerializer<Object> valueSerializer) {
        if (valueSerializer == null) {
            return rawValues;
        }
        return SerializationUtils.deserialize(rawValues, valueSerializer);
    }

    private Object deserializeValue(byte[] value, RedisSerializer<Object> valueSerializer) {
        if (valueSerializer == null) {
            return value;
        }
        return valueSerializer.deserialize(value);
    }

    public Object findByListCacheKey(List<CacheKey> ks) {
        return this.mGetByCacheKey(ks);
    }

    public Long hsize(String id) {
        return this.redisTemplate.opsForHash().size((Object)id);
    }

    public @Nullable Long bitCount(String key) {
        return (Long)this.redisTemplate.execute(connection -> connection.stringCommands().bitCount(RedisRepository.keySerialize(key)));
    }

    public @Nullable Long bitCount(String key, long start, long end) {
        return (Long)this.redisTemplate.execute(connection -> connection.stringCommands().bitCount(RedisRepository.keySerialize(key), start, end));
    }

    public <T> @Nullable Long publish(String channel, T message, Function<T, byte[]> mapper) {
        return (Long)this.redisTemplate.execute(redis -> {
            byte[] channelBytes = RedisRepository.keySerialize(channel);
            return redis.publish(channelBytes, (byte[])mapper.apply(message));
        });
    }

    public void subscribe(String channel, MessageListener listener) {
        this.redisTemplate.execute(redis -> {
            byte[] channelBytes = RedisRepository.keySerialize(channel);
            redis.subscribe(listener, (byte[][])new byte[][]{channelBytes});
            return null;
        });
    }

    public @Nullable Long bitCount(String key, long start, long end, RedisCommand.BitMapModel model) {
        return (Long)this.redisTemplate.execute(redis -> (Long)redis.execute("BITCOUNT", (byte[][])new byte[][]{RedisRepository.keySerialize(key), RedisRepository.keySerialize(Long.toString(start)), RedisRepository.keySerialize(Long.toString(end)), RedisRepository.keySerialize(model.name())}));
    }

    public @Nullable List<Long> bitField(String key, BitFieldSubCommands commands) {
        return (List)this.redisTemplate.execute(connection -> connection.stringCommands().bitField(RedisRepository.keySerialize(key), commands));
    }

    public @Nullable Long bitPos(String key, boolean bit) {
        return (Long)this.redisTemplate.execute(redis -> redis.bitPos(RedisRepository.keySerialize(key), bit));
    }

    public @Nullable Long bitPos(String key, boolean bit, Range<Long> range) {
        return (Long)this.redisTemplate.execute(connection -> connection.stringCommands().bitPos(RedisRepository.keySerialize(key), bit, range));
    }

    public @Nullable Boolean getBit(String key, long offset) {
        return (Boolean)this.redisTemplate.execute(connection -> connection.stringCommands().getBit(RedisRepository.keySerialize(key), offset));
    }

    public @Nullable Boolean setBit(String key, long offset, boolean value) {
        return (Boolean)this.redisTemplate.execute(connection -> connection.stringCommands().setBit(RedisRepository.keySerialize(key), offset, value));
    }

    public boolean getBit(String key, String param) {
        return Boolean.TRUE.equals(this.redisTemplate.opsForValue().getBit((Object)key, RedisRepository.hash(param)));
    }

    public Boolean setBit(String key, String param, boolean value) {
        return this.redisTemplate.opsForValue().setBit((Object)key, RedisRepository.hash(param), value);
    }

    private static long hash(String key) {
        return Math.abs(Hashing.murmur3_128().hashObject((Object)key, Funnels.stringFunnel((Charset)StandardCharsets.UTF_8)).asInt());
    }

    public static byte[] keySerialize(String redisKey) {
        return Objects.requireNonNull(RedisSerializer.string().serialize((Object)redisKey), "Redis key is null.");
    }

    public static String keyDeserialize(byte[] redisKey) {
        return Objects.requireNonNull((String)RedisSerializer.string().deserialize(redisKey), "Redis key is null.");
    }
}

