package com.kuma.boot.ddd.domain.handler;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.ddd.domain.support.DomainEventPublisher;
import com.kuma.boot.ddd.domain.support.TraceHandler;
import com.kuma.boot.ddd.model.domain.event.DefaultDomainEvent;
import java.nio.charset.StandardCharsets;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQListener;

public abstract class AbstractDomainEventHandler extends TraceHandler implements RocketMQListener {
   protected final DomainEventPublisher rocketMQDomainEventPublisher;

   protected AbstractDomainEventHandler(DomainEventPublisher rocketMQDomainEventPublisher) {
      this.rocketMQDomainEventPublisher = rocketMQDomainEventPublisher;
   }

   public void onMessage(MessageExt messageExt) {
      try {
         this.putTrace(messageExt);
         String msg = new String(messageExt.getBody(), StandardCharsets.UTF_8);
         this.handleDomainEvent(this.convert(msg));
      } catch (Exception e) {
         LogUtils.error("消费失败，主题Topic：{}，偏移量Offset：{}，错误信息：{}", new Object[]{messageExt.getTopic(), messageExt.getCommitLogOffset(), e.getMessage(), e});
         throw e;
      } finally {
         this.clearTrace();
      }

   }

   protected abstract void handleDomainEvent(DefaultDomainEvent domainEvent);

   protected abstract DefaultDomainEvent convert(String msg);
}
