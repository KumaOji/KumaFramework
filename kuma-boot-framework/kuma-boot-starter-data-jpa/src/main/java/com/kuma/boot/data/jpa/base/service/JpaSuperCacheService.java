package com.kuma.boot.data.jpa.base.service;

import com.kuma.boot.cache.redis.model.CacheKey;
import com.kuma.boot.data.jpa.base.entity.JpaSuperEntity;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import org.jspecify.annotations.NonNull;

public interface JpaSuperCacheService<T extends JpaSuperEntity<I>, I extends Serializable> extends JpaSuperService<T, I> {
   T getByIdCache(I id);

   T getByKey(CacheKey key, Function<CacheKey, I> loader);

   List<T> findByIds(@NonNull Collection<I> ids, Function<Collection<I>, Collection<T>> loader);

   void refreshCache();

   void clearCache();
}
