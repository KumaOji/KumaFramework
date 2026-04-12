package com.kuma.boot.ddd.domain.support;

import com.kuma.boot.ddd.model.domain.event.DomainEvent;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

@Component
public class RocketMQDomainEventPublisher implements DomainEventPublisher {
   private final RocketMQTemplate rocketMqTemplate;
   private final TraceUtil traceUtil;

   public RocketMQDomainEventPublisher(RocketMQTemplate rocketMqTemplate, TraceUtil traceUtil) {
      this.rocketMqTemplate = rocketMqTemplate;
      this.traceUtil = traceUtil;
   }

   public void publish(DomainEvent payload, boolean isTX) {
      if (isTX) {
      }

   }
}
