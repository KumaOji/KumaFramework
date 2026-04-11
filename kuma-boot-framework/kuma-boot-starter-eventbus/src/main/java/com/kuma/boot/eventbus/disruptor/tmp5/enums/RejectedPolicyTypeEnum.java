package com.kuma.boot.eventbus.disruptor.tmp5.enums;

import com.kuma.boot.eventbus.disruptor.tmp5.task.rejected.RejectedInvocationHandler;
import com.kuma.boot.eventbus.disruptor.tmp5.task.rejected.policy.RunsOldestTaskPolicy;
import com.kuma.boot.eventbus.disruptor.tmp5.task.rejected.policy.SyncPutQueuePolicy;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

public enum RejectedPolicyTypeEnum {
   CALLER_RUNS_POLICY(1, "CallerRunsPolicy", new ThreadPoolExecutor.CallerRunsPolicy()),
   ABORT_POLICY(2, "AbortPolicy", new ThreadPoolExecutor.AbortPolicy()),
   DISCARD_POLICY(3, "DiscardPolicy", new ThreadPoolExecutor.DiscardPolicy()),
   DISCARD_OLDEST_POLICY(4, "DiscardOldestPolicy", new ThreadPoolExecutor.DiscardOldestPolicy()),
   RUNS_OLDEST_TASK_POLICY(5, "RunsOldestTaskPolicy", new RunsOldestTaskPolicy()),
   SYNC_PUT_QUEUE_POLICY(6, "SyncPutQueuePolicy", new SyncPutQueuePolicy());

   private Integer type;
   private String name;
   private RejectedExecutionHandler rejectedHandler;

   public static RejectedExecutionHandler getRejectedPolicyTypeEnumByName(String name) {
      for(RejectedExecutionHandler handler : ServiceLoader.load(RejectedExecutionHandler.class)) {
         String handlerName = handler.getClass().getSimpleName();
         if (name.equalsIgnoreCase(handlerName)) {
            return handler;
         }
      }

      Optional<RejectedPolicyTypeEnum> rejectedTypeEnum = Stream.of(values()).filter((each) -> each.name.equals(name)).findFirst();
      if (rejectedTypeEnum.isPresent()) {
         return ((RejectedPolicyTypeEnum)rejectedTypeEnum.get()).rejectedHandler;
      } else {
         return CALLER_RUNS_POLICY.rejectedHandler;
      }
   }

   public static RejectedExecutionHandler getProxy(String name) {
      return getProxy(getRejectedPolicyTypeEnumByName(name));
   }

   public static RejectedExecutionHandler getProxy(RejectedExecutionHandler handler) {
      return (RejectedExecutionHandler)Proxy.newProxyInstance(handler.getClass().getClassLoader(), new Class[]{RejectedExecutionHandler.class}, new RejectedInvocationHandler(handler));
   }

   private RejectedPolicyTypeEnum(Integer type, String name, RejectedExecutionHandler rejectedHandler) {
      this.type = type;
      this.name = name;
      this.rejectedHandler = rejectedHandler;
   }

   public Integer getType() {
      return this.type;
   }

   public String getName() {
      return this.name;
   }

   public RejectedExecutionHandler getRejectedHandler() {
      return this.rejectedHandler;
   }

   // $FF: synthetic method
   private static RejectedPolicyTypeEnum[] $values() {
      return new RejectedPolicyTypeEnum[]{CALLER_RUNS_POLICY, ABORT_POLICY, DISCARD_POLICY, DISCARD_OLDEST_POLICY, RUNS_OLDEST_TASK_POLICY, SYNC_PUT_QUEUE_POLICY};
   }
}
