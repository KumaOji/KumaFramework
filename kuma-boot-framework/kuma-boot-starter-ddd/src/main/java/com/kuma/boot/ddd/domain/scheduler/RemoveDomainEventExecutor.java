package com.kuma.boot.ddd.domain.scheduler;

import com.kuma.boot.ddd.domain.support.TraceUtil;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

@Component
public class RemoveDomainEventExecutor {
   private final RocketMQTemplate rocketMqTemplate;
   private final TraceUtil traceUtil;

   public RemoveDomainEventExecutor(RocketMQTemplate rocketMqTemplate, TraceUtil traceUtil) {
      this.rocketMqTemplate = rocketMqTemplate;
      this.traceUtil = traceUtil;
   }

   public void execute(String serviceId) {
   }
}
