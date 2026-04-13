//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.ddd.domain.handler;

import cn.hutool.core.bean.BeanUtil;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.ddd.domain.handler.domainevent.RemoveCacheEvent;
import com.kuma.boot.ddd.domain.support.DomainEventPublisher;
import com.kuma.boot.ddd.model.domain.event.DefaultDomainEvent;
import java.util.List;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class RemoveCacheEventHandler extends AbstractDomainEventHandler {
   private final List<CacheManager> cacheManagers;

   public RemoveCacheEventHandler(DomainEventPublisher rocketMQDomainEventPublisher, List<CacheManager> cacheManagers) {
      super(rocketMQDomainEventPublisher);
      this.cacheManagers = cacheManagers;
   }

   protected void handleDomainEvent(DefaultDomainEvent domainEvent) {
      RemoveCacheEvent event = (RemoveCacheEvent)domainEvent;
      this.cacheManagers.forEach((item) -> {
         Cache cache = item.getCache(event.getName());
         if (ObjectUtils.isNotNull(cache)) {
            cache.evictIfPresent(event.getKey());
         }

      });
   }

   protected DefaultDomainEvent convert(String msg) {
      return (DefaultDomainEvent)BeanUtil.toBean(msg, RemoveCacheEvent.class);
   }
}
