package com.kuma.boot.data.jpa.base.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kuma.boot.cache.redis.model.CacheKey;
import com.kuma.boot.cache.redis.model.CacheKeyBuilder;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.data.jpa.base.entity.JpaSuperEntity;
import com.kuma.boot.data.jpa.base.repository.JpaSuperRepository;
import com.kuma.boot.data.jpa.base.service.JpaSuperCacheService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class JpaSuperCacheServiceImpl<R extends JpaSuperRepository<T, I>, T extends JpaSuperEntity<I>, I extends Serializable> extends JpaSuperServiceImpl<R, T, I> implements JpaSuperCacheService<T, I> {
   @Autowired
   protected RedisRepository redisRepository;
   protected static final int MAX_BATCH_KEY_SIZE = 20;

   public JpaSuperCacheServiceImpl() {
   }

   protected abstract CacheKeyBuilder cacheKeyBuilder();

   @Transactional(
      readOnly = true
   )
   public T getByIdCache(I id) {
      CacheKey cacheKey = this.cacheKeyBuilder().key(new Object[]{id});
      return (T)(this.redisRepository.get(cacheKey, (k) -> (JpaSuperEntity)this.repository().getReferenceById(id), new boolean[0]));
   }

   @Transactional(
      readOnly = true
   )
   public List<T> findByIds(@NonNull Collection<I> ids, Function<Collection<I>, Collection<T>> loader) {
      if (ids.isEmpty()) {
         return Collections.emptyList();
      } else {
         Stream var10000 = ids.stream();
         CacheKeyBuilder var10001 = this.cacheKeyBuilder();
         Objects.requireNonNull(var10001);
         CacheKeyBuilder var4 = var10001;
         List<CacheKey> keys = var10000.map((xva$0) -> var4.key(new Object[]{xva$0})).toList();
         List<List<CacheKey>> partitionKeys = Lists.partition(keys, 20);
         List<T> valueList = new ArrayList();
         List<I> keysList = Lists.newArrayList(ids);
         Set<I> missedKeys = Sets.newLinkedHashSet();
         List<T> allList = new ArrayList();

         for(int i = 0; i < valueList.size(); ++i) {
            T v = (T)(valueList.get(i));
            I k = (I)(keysList.get(i));
            if (v == null) {
               missedKeys.add(k);
            } else {
               allList.add(v);
            }
         }

         if (CollUtil.isNotEmpty(missedKeys)) {
            Collection<T> missList = (Collection)loader.apply(missedKeys);
            missList.forEach(this::setCache);
            allList.addAll(missList);
         }

         return allList;
      }
   }

   @Transactional(
      readOnly = true
   )
   public T getByKey(CacheKey key, Function<CacheKey, I> loader) {
      I id = (I)(this.redisRepository.get(key, loader, new boolean[0]));
      return (T)(id == null ? null : this.getByIdCache(id));
   }

   public void refreshCache() {
      this.repository().findAll().forEach(this::setCache);
   }

   public void clearCache() {
      this.repository().findAll().forEach(this::delCache);
   }

   protected void delCache(Serializable... ids) {
      this.delCache((Collection)Arrays.asList(ids));
   }

   protected void delCache(Collection<? extends Serializable> idList) {
      CacheKey[] keys = (CacheKey[])idList.stream().map((id) -> this.cacheKeyBuilder().key(new Object[]{id})).toArray((x$0) -> new CacheKey[x$0]);
      this.redisRepository.del(keys);
   }

   protected void delCache(T model) {
      Object id = this.getId(model);
      if (id != null) {
         CacheKey key = this.cacheKeyBuilder().key(new Object[]{id});
         this.redisRepository.del(new CacheKey[]{key});
      }

   }

   protected void setCache(T model) {
      Object id = this.getId(model);
      if (id != null) {
         CacheKey key = this.cacheKeyBuilder().key(new Object[]{id});
         this.redisRepository.set(key, model, new boolean[0]);
      }

   }

   protected Object getId(T model) {
      return model instanceof JpaSuperEntity ? model.getId() : null;
   }
}
