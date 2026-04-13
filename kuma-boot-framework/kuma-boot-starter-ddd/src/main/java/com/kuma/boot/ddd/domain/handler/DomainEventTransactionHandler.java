//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.ddd.domain.handler;

import cn.hutool.core.bean.BeanUtil;
import com.kuma.boot.ddd.domain.model.DomainEventA;
import com.kuma.boot.ddd.domain.service.DomainEventService;
import com.kuma.boot.ddd.domain.support.AbstractTransactionHandler;
import com.kuma.boot.ddd.model.domain.event.DefaultDomainEvent;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@RocketMQTransactionListener(
        corePoolSize = 16,
        maximumPoolSize = 32
)
public class DomainEventTransactionHandler extends AbstractTransactionHandler {
   private final DomainEventService domainEventService;

   public DomainEventTransactionHandler(DomainEventService domainEventService) {
      this.domainEventService = domainEventService;
   }

   protected void executeExtLocalTransaction(Message message, Object args) {
      byte[] payload = (byte[])message.getPayload();
      this.domainEventService.create(new DomainEventA(payload, (DefaultDomainEvent)BeanUtil.toBean(payload, DefaultDomainEvent.class)));
   }

   protected boolean checkExtLocalTransaction(Message message) {
      Object obj = message.getHeaders().get("TRANSACTION_ID");
      Assert.notNull(obj, "事务ID不为空");
      return this.domainEventService.countById(Long.parseLong(obj.toString())) > 0L;
   }
}
