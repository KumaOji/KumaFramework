package com.kuma.boot.data.jpa.hibernate.spi.cache;

import cn.hutool.extra.spring.SpringUtil;
import java.util.Map;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.cache.spi.support.RegionFactoryTemplate;
import org.hibernate.cache.spi.support.StorageAccess;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.springframework.cache.CacheManager;

public class HerodotusRegionFactory extends RegionFactoryTemplate {
   private CacheManager cacheManager;

   public HerodotusRegionFactory() {
   }

   protected StorageAccess createQueryResultsRegionStorageAccess(String regionName, SessionFactoryImplementor sessionFactory) {
      return new HerodotusDomainDataStorageAccess(this.cacheManager.getCache(regionName));
   }

   protected StorageAccess createTimestampsRegionStorageAccess(String regionName, SessionFactoryImplementor sessionFactory) {
      return new HerodotusDomainDataStorageAccess(this.cacheManager.getCache(regionName));
   }

   protected DomainDataStorageAccess createDomainDataStorageAccess(DomainDataRegionConfig regionConfig, DomainDataRegionBuildingContext buildingContext) {
      return new HerodotusDomainDataStorageAccess(this.cacheManager.getCache(regionConfig.getRegionName()));
   }

   protected void prepareForUse(SessionFactoryOptions settings, Map configValues) {
      this.cacheManager = (CacheManager)SpringUtil.getBean("herodotusCacheManager");
   }

   protected void releaseFromUse() {
      this.cacheManager = null;
   }
}
