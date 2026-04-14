package com.kuma.boot.ddd.domain.support;

import com.kuma.boot.ddd.model.domain.event.DomainEvent;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * 未启用 RocketMQ（无 {@link RocketMQTemplate} Bean）时的 {@link DomainEventPublisher} 占位实现，
 * 避免排除 {@code RocketMQAutoConfiguration} 时其它组件因缺少发布器而启动失败。
 */
@Component
@ConditionalOnMissingBean(RocketMQTemplate.class)
public class NoOpDomainEventPublisher implements DomainEventPublisher {

   @Override
   public void publish( DomainEvent payload, boolean isTX ) {
   }
}
