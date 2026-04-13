//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.ddd.domain.handler;

import cn.hutool.core.bean.BeanUtil;
import com.kuma.boot.ddd.domain.service.DomainEventService;
import com.kuma.boot.ddd.domain.support.DomainEventPublisher;
import com.kuma.boot.ddd.model.domain.event.DefaultDomainEvent;
import org.springframework.stereotype.Component;

@Component
public class RemoveDomainEventHandler extends AbstractDomainEventHandler {
   private final DomainEventService domainEventService;

   public RemoveDomainEventHandler(DomainEventPublisher rocketMQDomainEventPublisher, DomainEventService domainEventService) {
      super(rocketMQDomainEventPublisher);
      this.domainEventService = domainEventService;
   }

   protected void handleDomainEvent(DefaultDomainEvent domainEvent) {
      this.domainEventService.deleteOldByServiceIdOfThreeMonths(domainEvent.getServiceId());
   }

   protected DefaultDomainEvent convert(String msg) {
      return (DefaultDomainEvent)BeanUtil.toBean(msg, DefaultDomainEvent.class);
   }
}
