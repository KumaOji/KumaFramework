package com.kuma.boot.data.jpa.hibernate.spi.cache;

import cn.hutool.crypto.SecureUtil;
import com.kuma.boot.common.holder.TenantContextHolder;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

public class HerodotusDomainDataStorageAccess implements DomainDataStorageAccess {
   private static final Logger log = LoggerFactory.getLogger(HerodotusDomainDataStorageAccess.class);
   private Cache cache;

   public HerodotusDomainDataStorageAccess() {
   }

   public HerodotusDomainDataStorageAccess(Cache cache) {
      this.cache = cache;
   }

   private String secure(Object key) {
      String original = String.valueOf(key);
      if (StringUtils.isNotBlank(original) && StringUtils.startWith(original, "sql:")) {
         String recent = SecureUtil.md5(original);
         log.trace("[ttc] |- SPI - Secure the sql type key [{}] to [{}]", original, recent);
         return recent;
      } else {
         return original;
      }
   }

   private String getTenantId() {
      String tenantId = TenantContextHolder.getTenant();
      String result = StringUtils.isNotBlank(tenantId) ? tenantId : "tenant_id";
      log.trace("[ttc] |- SPI - Tenant identifier for jpa second level cache is : [{}]", result);
      return StringUtils.toLowerCase(result);
   }

   private String wrapper(Object key) {
      String original = this.secure(key);
      String tenantId = this.getTenantId();
      String result = tenantId + "_" + original;
      log.trace("[ttc] |- SPI - Current cache key is : [{}]", result);
      return result;
   }

   private Object get(Object key) {
      Cache.ValueWrapper value = this.cache.get(key);
      return ObjectUtils.isNotEmpty(value) ? value.get() : null;
   }

   public boolean contains(Object key) {
      String wrapperKey = this.wrapper(key);
      Object value = this.get(wrapperKey);
      log.trace("[ttc] |- SPI - check is key : [{}] exist.", wrapperKey);
      return ObjectUtils.isNotEmpty(value);
   }

   public Object getFromCache(Object key, SharedSessionContractImplementor session) {
      String wrapperKey = this.wrapper(key);
      Object value = this.get(wrapperKey);
      log.trace("[ttc] |- SPI - get from cache key is : [{}], value is : [{}]", wrapperKey, value);
      return value;
   }

   public void putIntoCache(Object key, Object value, SharedSessionContractImplementor session) {
      String wrapperKey = this.wrapper(key);
      log.trace("[ttc] |- SPI - put into cache key is : [{}], value is : [{}]", wrapperKey, value);
      this.cache.put(wrapperKey, value);
   }

   public void removeFromCache(Object key, SharedSessionContractImplementor session) {
      String wrapperKey = this.wrapper(key);
      log.trace("[ttc] |- SPI - remove from cache key is : [{}]", wrapperKey);
      this.cache.evict(wrapperKey);
   }

   public void evictData(Object key) {
      String wrapperKey = this.wrapper(key);
      log.trace("[ttc] |- SPI - evict key : [{}] from cache.", wrapperKey);
      this.cache.evict(wrapperKey);
   }

   public void clearCache(SharedSessionContractImplementor session) {
      this.evictData();
   }

   public void evictData() {
      log.trace("[ttc] |- SPI - clear all cache data.");
      this.cache.clear();
   }

   public void release() {
      log.trace("[ttc] |- SPI - cache release.");
      this.cache.invalidate();
   }
}
