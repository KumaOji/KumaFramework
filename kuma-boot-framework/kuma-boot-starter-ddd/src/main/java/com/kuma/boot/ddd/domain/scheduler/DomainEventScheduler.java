package com.kuma.boot.ddd.domain.scheduler;

import com.kuma.boot.core.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DomainEventScheduler {
   private final ContextUtils contextUtils;
   private final RemoveDomainEventExecutor removeDomainEventExecutor;

   public DomainEventScheduler(ContextUtils contextUtils, RemoveDomainEventExecutor removeDomainEventExecutor) {
      this.contextUtils = contextUtils;
      this.removeDomainEventExecutor = removeDomainEventExecutor;
   }

   @Scheduled(
      cron = "0 0 1 * * ?"
   )
   public void removeDomainEvent() {
      try {
         ContextUtils var10001 = this.contextUtils;
         this.removeDomainEventExecutor.execute(ContextUtils.getServiceId());
      } catch (Exception e) {
         LogUtils.error("定时执行删除前三个月的领域事件任务失败，错误信息：{}", new Object[]{e.getMessage(), e});
      }

   }
}
