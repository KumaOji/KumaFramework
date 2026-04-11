package com.kuma.boot.eventbus.disruptor.tmp5.enums;

import cn.hutool.core.lang.Tuple;
import com.lmax.disruptor.EventFactory;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.eventbus.disruptor.tmp5.builder.WaitStrategyBuilder;
import com.kuma.boot.eventbus.disruptor.tmp5.dto.DisruptorCreate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum DisruptorCreateMethodEnum {
   CREATE0("create0", new ConcurrentHashMap(), new ConcurrentHashMap(), new ConcurrentHashMap(), new ConcurrentHashMap(), new ConcurrentHashMap()),
   CREATE1("create1", new ConcurrentHashMap(), new ConcurrentHashMap(), new ConcurrentHashMap(), new ConcurrentHashMap(), new ConcurrentHashMap()),
   CREATE2("create2", new ConcurrentHashMap(), new ConcurrentHashMap(), new ConcurrentHashMap(), new ConcurrentHashMap(), new ConcurrentHashMap()),
   CREATE3("create3", new ConcurrentHashMap(), new ConcurrentHashMap(), new ConcurrentHashMap(), new ConcurrentHashMap(), new ConcurrentHashMap()),
   CREATE4("create4", new ConcurrentHashMap(), new ConcurrentHashMap(), new ConcurrentHashMap(), new ConcurrentHashMap(), new ConcurrentHashMap());

   private String name;
   private ConcurrentHashMap<String, DisruptorCreate> dcbMaps;
   private ConcurrentHashMap<String, WaitStrategyBuilder> waitStrategyMaps;
   private ConcurrentHashMap<String, EventFactory> eventFactoryMaps;
   private ConcurrentHashMap<String, ThreadFactory> threadFactoryMaps;
   private ConcurrentHashMap<String, Executor> executorMaps;

   public static DisruptorCreateMethodEnum getByName(String createMethodName) {
      if (StringUtils.isBlank(createMethodName)) {
         throw new RuntimeException("DisruptorCreateMethodEnum.getByName.createMethodName\u4e0d\u4e3a\u7a7a!");
      } else {
         return (DisruptorCreateMethodEnum)Stream.of(values()).filter((each) -> each.name.equals(createMethodName)).findFirst().orElseThrow(() -> new RuntimeException("\u6839\u636ecreateMethodName\u672a\u5339\u914d\u5230DisruptorCreatMethodEnum,\u8bf7\u68c0\u67e5createMethodName\u662f\u5426\u6b63\u786e!"));
      }
   }

   public static void checkKeyFormat(String key) {
      if (StringUtils.isBlank(key)) {
         throw new RuntimeException("DisruptorCreateMethodEnum.checkKeyFormat.key\u4e0d\u4e3a\u7a7a!");
      } else {
         boolean isOk = false;

         for(DisruptorCreateMethodEnum dcm : (List)Stream.of(values()).collect(Collectors.toList())) {
            if (key.indexOf(dcm.getName()) == 0) {
               isOk = true;
               break;
            }
         }

         if (!isOk) {
            throw new RuntimeException("DisruptorCreateMethodEnum.checkKeyFormat.key\u683c\u5f0f\u4e0d\u6b63\u786e!");
         }
      }
   }

   public static DisruptorCreate createDcbMaps(Tuple tuple) {
      checkParams("DisruptorCreateMethodEnum.createDcbMaps", tuple);
      String key = (String)tuple.get(1);
      DisruptorCreateMethodEnum disruptorCreateMethodEnum = (DisruptorCreateMethodEnum)tuple.get(2);
      ConcurrentHashMap<String, DisruptorCreate> bizNameDcbMaps = disruptorCreateMethodEnum.getDcbMaps();
      if (Objects.nonNull(bizNameDcbMaps.get(key))) {
         return (DisruptorCreate)bizNameDcbMaps.get(key);
      } else {
         DisruptorCreate disruptorCreate = new DisruptorCreate();
         bizNameDcbMaps.put(key, disruptorCreate);
         ConcurrentHashMap<String, WaitStrategyBuilder> waitStrategyMaps = disruptorCreateMethodEnum.getWaitStrategyMaps();
         WaitStrategyBuilder waitStrategyBuilder = (WaitStrategyBuilder)waitStrategyMaps.get(key);
         if (Objects.nonNull(waitStrategyBuilder)) {
            disruptorCreate.setWaitStrategy(waitStrategyBuilder.createWaitStrategy());
         }

         ConcurrentHashMap<String, EventFactory> eventFactoryMaps = disruptorCreateMethodEnum.getEventFactoryMaps();
         EventFactory eventFactory = (EventFactory)eventFactoryMaps.get(key);
         if (Objects.nonNull(eventFactory)) {
            disruptorCreate.setEventFactory(eventFactory);
         }

         ConcurrentHashMap<String, ThreadFactory> threadFactoryMaps = disruptorCreateMethodEnum.getThreadFactoryMaps();
         ThreadFactory threadFactory = (ThreadFactory)threadFactoryMaps.get(key);
         if (Objects.nonNull(threadFactory)) {
            disruptorCreate.setThreadFactory(threadFactory);
         }

         ConcurrentHashMap<String, Executor> executorMaps = disruptorCreateMethodEnum.getExecutorMaps();
         Executor executor = (Executor)executorMaps.get(key);
         if (Objects.nonNull(executor)) {
         }

         return disruptorCreate;
      }
   }

   public static WaitStrategyBuilder createWaitStrategyMaps(Tuple tuple) {
      checkParams("DisruptorCreateMethodEnum.createWaitStrategyMaps", tuple);
      String key = (String)tuple.get(1);
      DisruptorCreateMethodEnum disruptorCreateMethodEnum = (DisruptorCreateMethodEnum)tuple.get(2);
      ConcurrentHashMap<String, WaitStrategyBuilder> waitStrategyMaps = disruptorCreateMethodEnum.getWaitStrategyMaps();
      if (Objects.nonNull(waitStrategyMaps.get(key))) {
         return (WaitStrategyBuilder)waitStrategyMaps.get(key);
      } else {
         WaitStrategyBuilder waitStrategyBuilder = new WaitStrategyBuilder();
         waitStrategyMaps.put(key, waitStrategyBuilder);
         return waitStrategyBuilder;
      }
   }

   public static EventFactory createEventFactoryMaps(Tuple tuple, EventFactory eventFactory) {
      checkParams("DisruptorCreateMethodEnum.createEventFactoryMaps", tuple);
      if (Objects.isNull(eventFactory)) {
         throw new RuntimeException("DisruptorCreateMethodEnum.createEventFactoryMaps.eventFactory\u4e0d\u4e3a\u7a7a!");
      } else {
         String key = (String)tuple.get(1);
         DisruptorCreateMethodEnum disruptorCreateMethodEnum = (DisruptorCreateMethodEnum)tuple.get(2);
         ConcurrentHashMap<String, EventFactory> eventFactoryMaps = disruptorCreateMethodEnum.getEventFactoryMaps();
         if (Objects.nonNull(eventFactoryMaps.get(key))) {
            return (EventFactory)eventFactoryMaps.get(key);
         } else {
            eventFactoryMaps.put(key, eventFactory);
            return eventFactory;
         }
      }
   }

   public static ThreadFactory createThreadFactoryMaps(Tuple tuple, ThreadFactory threadFactory) {
      checkParams("DisruptorCreateMethodEnum.createThreadFactoryMaps", tuple);
      if (Objects.isNull(threadFactory)) {
         throw new RuntimeException("DisruptorCreateMethodEnum.createThreadFactoryMaps.threadFactory\u4e0d\u4e3a\u7a7a!");
      } else {
         String key = (String)tuple.get(1);
         DisruptorCreateMethodEnum disruptorCreateMethodEnum = (DisruptorCreateMethodEnum)tuple.get(2);
         ConcurrentHashMap<String, ThreadFactory> bizNameDcbMaps = disruptorCreateMethodEnum.getThreadFactoryMaps();
         if (Objects.nonNull(bizNameDcbMaps.get(key))) {
            return (ThreadFactory)bizNameDcbMaps.get(key);
         } else {
            bizNameDcbMaps.put(key, threadFactory);
            return threadFactory;
         }
      }
   }

   public static Executor createExecutorMaps(Tuple tuple, Executor executor) {
      checkParams("DisruptorCreateMethodEnum.createExecutorMaps", tuple);
      if (Objects.isNull(executor)) {
         throw new RuntimeException("DisruptorCreateMethodEnum.createExecutorMaps.executor!");
      } else {
         String key = (String)tuple.get(1);
         DisruptorCreateMethodEnum disruptorCreateMethodEnum = (DisruptorCreateMethodEnum)tuple.get(2);
         ConcurrentHashMap<String, Executor> bizNameDcbMaps = disruptorCreateMethodEnum.getExecutorMaps();
         if (Objects.nonNull(bizNameDcbMaps.get(key))) {
            return (Executor)bizNameDcbMaps.get(key);
         } else {
            bizNameDcbMaps.put(key, executor);
            return executor;
         }
      }
   }

   private static void checkParams(String methodName, Tuple tuple) {
      if (Objects.isNull(tuple)) {
         throw new RuntimeException(methodName + ".bizName\u4e0d\u4e3a\u7a7a\uff01");
      } else if (Objects.isNull(tuple.get(0))) {
         throw new RuntimeException(methodName + ".tuple\u7b2c\u4e00\u4e2a\u53c2\u6570\u4e0d\u4e3a\u7a7a\uff01");
      } else if (Objects.isNull(tuple.get(1))) {
         throw new RuntimeException(methodName + ".tuple\u7b2c\u4e8c\u4e2a\u53c2\u6570\u4e0d\u4e3a\u7a7a\uff01");
      } else if (Objects.isNull(tuple.get(2))) {
         throw new RuntimeException(methodName + ".tuple\u7b2c\u4e09\u4e2a\u53c2\u6570\u4e0d\u4e3a\u7a7a\uff01");
      }
   }

   private DisruptorCreateMethodEnum(String name, ConcurrentHashMap<String, DisruptorCreate> dcbMaps, ConcurrentHashMap<String, WaitStrategyBuilder> waitStrategyMaps, ConcurrentHashMap<String, EventFactory> eventFactoryMaps, ConcurrentHashMap<String, ThreadFactory> threadFactoryMaps, ConcurrentHashMap<String, Executor> executorMaps) {
      this.name = name;
      this.dcbMaps = dcbMaps;
      this.waitStrategyMaps = waitStrategyMaps;
      this.eventFactoryMaps = eventFactoryMaps;
      this.threadFactoryMaps = threadFactoryMaps;
      this.executorMaps = executorMaps;
   }

   public String getName() {
      return this.name;
   }

   public ConcurrentHashMap<String, DisruptorCreate> getDcbMaps() {
      return this.dcbMaps;
   }

   public ConcurrentHashMap<String, WaitStrategyBuilder> getWaitStrategyMaps() {
      return this.waitStrategyMaps;
   }

   public ConcurrentHashMap<String, EventFactory> getEventFactoryMaps() {
      return this.eventFactoryMaps;
   }

   public ConcurrentHashMap<String, ThreadFactory> getThreadFactoryMaps() {
      return this.threadFactoryMaps;
   }

   public ConcurrentHashMap<String, Executor> getExecutorMaps() {
      return this.executorMaps;
   }

   // $FF: synthetic method
   private static DisruptorCreateMethodEnum[] $values() {
      return new DisruptorCreateMethodEnum[]{CREATE0, CREATE1, CREATE2, CREATE3, CREATE4};
   }
}
