package com.kuma.boot.eventbus.disruptor.tmp5.service;

import cn.hutool.core.lang.Tuple;
import cn.hutool.core.lang.copier.Copier;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.EventTranslatorThreeArg;
import com.lmax.disruptor.EventTranslatorTwoArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.eventbus.disruptor.tmp5.dto.DisruptorCreate;
import com.kuma.boot.eventbus.disruptor.tmp5.enums.DisruptorCreateMethodEnum;
import com.kuma.boot.eventbus.disruptor.tmp5.event.DisruptorEvent;
import com.kuma.boot.eventbus.disruptor.tmp5.factory.DisruptorEventFactory;
import com.kuma.boot.eventbus.disruptor.tmp5.handler.DisruptorHandler;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

public class DisruptorService {
   private ConcurrentHashMap<String, Tuple> disruptorMaps = new ConcurrentHashMap();
   private ConcurrentHashMap<String, Tuple> keyMaps = new ConcurrentHashMap();

   public DisruptorService() {
   }

   public Tuple buildKey(DisruptorCreateMethodEnum disruptorCreateMethodEnum, String bizName) {
      if (Objects.isNull(disruptorCreateMethodEnum)) {
         throw new RuntimeException("DisruptorService.buildKey.disruptorCreateMethodEnum不为空!");
      } else if (StringUtils.isBlank(bizName)) {
         throw new RuntimeException("DisruptorService.buildKey.bizName不为空!");
      } else {
         String var10000 = disruptorCreateMethodEnum.getName();
         String key = var10000 + bizName;
         Tuple tuple = (Tuple)this.keyMaps.get(key);
         if (Objects.nonNull(tuple)) {
            return tuple;
         } else {
            tuple = new Tuple(new Object[]{bizName, key, disruptorCreateMethodEnum});
            this.keyMaps.put(key, tuple);
            return tuple;
         }
      }
   }

   public Tuple create0(String key) {
      if (StringUtils.isBlank(key)) {
         throw new RuntimeException("DisruptorService.create0.key不为空!");
      } else {
         DisruptorCreateMethodEnum.checkKeyFormat(key);
         ConcurrentHashMap<String, DisruptorCreate> bizNameDcbMaps = DisruptorCreateMethodEnum.CREATE0.getDcbMaps();
         DisruptorCreate dcb = (DisruptorCreate)bizNameDcbMaps.get(key);
         if (Objects.isNull(dcb)) {
            throw new RuntimeException("DisruptorService.create0.请先设置DisruptorCreatMethodEnum的createDcbMaps对应关系!");
         } else {
            int bufferSize = dcb.getBufferSize();
            WaitStrategy waitStrategy = dcb.getWaitStrategy();
            if (Objects.isNull(bufferSize)) {
               throw new RuntimeException("DisruptorService.create0.bufferSize不为空,2的整数次幂 比如1024,2048,,,,,!");
            } else if (Objects.isNull(waitStrategy)) {
               throw new RuntimeException("DisruptorService.create0.waitStrategy等待策略不为空-- 2的整数次幂 比如1024,2048,,,,!");
            } else if (Objects.nonNull(this.disruptorMaps.get(key))) {
               return (Tuple)this.disruptorMaps.get(key);
            } else {
               Copier<DisruptorEventFactory> cp = DisruptorEventFactory::new;
               DisruptorEventFactory eventFactory = (DisruptorEventFactory)cp.copy();
               Disruptor<DisruptorEvent> disruptor = new Disruptor(eventFactory, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, waitStrategy);
               Tuple tuple = new Tuple(new Object[]{disruptor, disruptor.getRingBuffer(), new AtomicBoolean(false)});
               this.disruptorMaps.put(key, tuple);
               return tuple;
            }
         }
      }
   }

   public Tuple create1(String key) {
      if (StringUtils.isBlank(key)) {
         throw new RuntimeException("DisruptorService.create1.key不为空!");
      } else {
         DisruptorCreateMethodEnum.checkKeyFormat(key);
         ConcurrentHashMap<String, DisruptorCreate> bizNameDcbMaps = DisruptorCreateMethodEnum.CREATE1.getDcbMaps();
         DisruptorCreate dcb = (DisruptorCreate)bizNameDcbMaps.get(key);
         if (Objects.isNull(dcb)) {
            throw new RuntimeException("DisruptorService.create1.请先设置DisruptorCreatMethodEnum的createDcbMaps对应关系!");
         } else {
            int bufferSize = dcb.getBufferSize();
            ProducerType producerType = dcb.getProducerType();
            WaitStrategy waitStrategy = dcb.getWaitStrategy();
            if (Objects.isNull(bufferSize)) {
               throw new RuntimeException("DisruptorService.create1.bufferSize不为空,2的整数次幂 比如1024,2048,,,,,!");
            } else if (Objects.isNull(producerType)) {
               throw new RuntimeException("DisruptorService.create1.producerType生产者类型不为空!");
            } else if (Objects.isNull(waitStrategy)) {
               throw new RuntimeException("DisruptorService.create1.waitStrategy等待策略不为空-- 2的整数次幂 比如1024,2048,,,,!");
            } else if (Objects.nonNull(this.disruptorMaps.get(key))) {
               return (Tuple)this.disruptorMaps.get(key);
            } else {
               Copier<DisruptorEventFactory> cp = DisruptorEventFactory::new;
               DisruptorEventFactory eventFactory = (DisruptorEventFactory)cp.copy();
               Disruptor<DisruptorEvent> disruptor = null;
               if (producerType.equals(ProducerType.MULTI)) {
                  disruptor = new Disruptor(eventFactory, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.MULTI, waitStrategy);
               } else {
                  disruptor = new Disruptor(eventFactory, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, waitStrategy);
               }

               Tuple tuple = new Tuple(new Object[]{disruptor, disruptor.getRingBuffer(), new AtomicBoolean(false)});
               this.disruptorMaps.put(key, tuple);
               return tuple;
            }
         }
      }
   }

   public Tuple create2(String key) {
      if (StringUtils.isBlank(key)) {
         throw new RuntimeException("DisruptorService.create2.key不为空!");
      } else {
         DisruptorCreateMethodEnum.checkKeyFormat(key);
         ConcurrentHashMap<String, DisruptorCreate> bizNameDcbMaps = DisruptorCreateMethodEnum.CREATE2.getDcbMaps();
         DisruptorCreate dcb = (DisruptorCreate)bizNameDcbMaps.get(key);
         if (Objects.isNull(dcb)) {
            throw new RuntimeException("DisruptorService.create2.请先设置DisruptorCreatMethodEnum的createDcbMaps对应关系!");
         } else {
            int bufferSize = dcb.getBufferSize();
            EventFactory eventFactory = dcb.getEventFactory();
            ProducerType producerType = dcb.getProducerType();
            WaitStrategy waitStrategy = dcb.getWaitStrategy();
            if (Objects.isNull(eventFactory)) {
               throw new RuntimeException("DisruptorService.create2.eventFactory自定义事件工厂不为空!");
            } else if (Objects.isNull(bufferSize)) {
               throw new RuntimeException("DisruptorService.create2.bufferSize不为空,2的整数次幂 比如1024,2048,,,,,!");
            } else if (Objects.isNull(producerType)) {
               throw new RuntimeException("DisruptorService.create2.producerType生产者类型不为空!");
            } else if (Objects.isNull(waitStrategy)) {
               throw new RuntimeException("DisruptorService.create2.waitStrategy等待策略不为空-- 2的整数次幂 比如1024,2048,,,,!");
            } else if (Objects.nonNull(this.disruptorMaps.get(key))) {
               return (Tuple)this.disruptorMaps.get(key);
            } else {
               Disruptor<DisruptorEvent> disruptor = null;
               if (producerType.equals(ProducerType.MULTI)) {
                  disruptor = new Disruptor(eventFactory, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.MULTI, waitStrategy);
               } else {
                  disruptor = new Disruptor(eventFactory, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, waitStrategy);
               }

               Tuple tuple = new Tuple(new Object[]{disruptor, disruptor.getRingBuffer(), new AtomicBoolean(false)});
               this.disruptorMaps.put(key, tuple);
               return tuple;
            }
         }
      }
   }

   public Tuple create3(String key) {
      if (StringUtils.isBlank(key)) {
         throw new RuntimeException("DisruptorService.create3.key不为空!");
      } else {
         DisruptorCreateMethodEnum.checkKeyFormat(key);
         ConcurrentHashMap<String, DisruptorCreate> bizNameDcbMaps = DisruptorCreateMethodEnum.CREATE3.getDcbMaps();
         DisruptorCreate dcb = (DisruptorCreate)bizNameDcbMaps.get(key);
         if (Objects.isNull(dcb)) {
            throw new RuntimeException("DisruptorService.create3.请先设置DisruptorCreatMethodEnum的createDcbMaps对应关系!");
         } else {
            int bufferSize = dcb.getBufferSize();
            EventFactory eventFactory = dcb.getEventFactory();
            ThreadFactory threadFactory = dcb.getThreadFactory();
            ProducerType producerType = dcb.getProducerType();
            WaitStrategy waitStrategy = dcb.getWaitStrategy();
            if (Objects.isNull(eventFactory)) {
               throw new RuntimeException("DisruptorService.create3.eventFactory自定义事件工厂不为空!");
            } else if (Objects.isNull(threadFactory)) {
               throw new RuntimeException("DisruptorService.create3.threadFactory自定义线程工厂不为空!");
            } else if (Objects.isNull(bufferSize)) {
               throw new RuntimeException("DisruptorService.create3.bufferSize不为空,2的整数次幂 比如1024,2048,,,,,!");
            } else if (Objects.isNull(producerType)) {
               throw new RuntimeException("DisruptorService.create3.producerType生产者类型不为空!");
            } else if (Objects.isNull(waitStrategy)) {
               throw new RuntimeException("DisruptorService.create3.waitStrategy等待策略不为空-- 2的整数次幂 比如1024,2048,,,,!");
            } else if (Objects.nonNull(this.disruptorMaps.get(key))) {
               return (Tuple)this.disruptorMaps.get(key);
            } else {
               Disruptor<DisruptorEvent> disruptor = null;
               if (producerType.equals(ProducerType.MULTI)) {
                  disruptor = new Disruptor(eventFactory, bufferSize, threadFactory, ProducerType.MULTI, waitStrategy);
               } else {
                  disruptor = new Disruptor(eventFactory, bufferSize, threadFactory, ProducerType.SINGLE, waitStrategy);
               }

               Tuple tuple = new Tuple(new Object[]{disruptor, disruptor.getRingBuffer(), new AtomicBoolean(false)});
               this.disruptorMaps.put(key, tuple);
               return tuple;
            }
         }
      }
   }

   public Tuple create4(String key) {
      if (StringUtils.isBlank(key)) {
         throw new RuntimeException("DisruptorService.create4.key不为空!");
      } else {
         DisruptorCreateMethodEnum.checkKeyFormat(key);
         ConcurrentHashMap<String, DisruptorCreate> bizNameDcbMaps = DisruptorCreateMethodEnum.CREATE4.getDcbMaps();
         DisruptorCreate dcb = (DisruptorCreate)bizNameDcbMaps.get(key);
         if (Objects.isNull(dcb)) {
            throw new RuntimeException("DisruptorService.create4.请先设置DisruptorCreatMethodEnum的createDcbMaps对应关系!");
         } else {
            int bufferSize = dcb.getBufferSize();
            EventFactory eventFactory = dcb.getEventFactory();
            ProducerType producerType = dcb.getProducerType();
            WaitStrategy waitStrategy = dcb.getWaitStrategy();
            if (Objects.isNull(eventFactory)) {
               throw new RuntimeException("DisruptorService.create4.eventFactory自定义事件工厂不为空!");
            } else if (Objects.isNull(bufferSize)) {
               throw new RuntimeException("DisruptorService.create4.bufferSize不为空,2的整数次幂 比如1024,2048,,,,,!");
            } else if (Objects.isNull(producerType)) {
               throw new RuntimeException("DisruptorService.create4.producerType生产者类型不为空!");
            } else if (Objects.isNull(waitStrategy)) {
               throw new RuntimeException("DisruptorService.create3.waitStrategy等待策略不为空-- 2的整数次幂 比如1024,2048,,,,!");
            } else if (Objects.nonNull(this.disruptorMaps.get(key))) {
               return (Tuple)this.disruptorMaps.get(key);
            } else {
               Disruptor<DisruptorEvent> disruptor = null;
               if (producerType.equals(ProducerType.MULTI)) {
                  disruptor = new Disruptor(eventFactory, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.MULTI, waitStrategy);
               } else {
                  disruptor = new Disruptor(eventFactory, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, waitStrategy);
               }

               Tuple tuple = new Tuple(new Object[]{disruptor, disruptor.getRingBuffer(), new AtomicBoolean(false)});
               this.disruptorMaps.put(key, tuple);
               return tuple;
            }
         }
      }
   }

   public Tuple createAddHandlerStart1(String key, DisruptorHandler disruptorHandler) {
      if (StringUtils.isBlank(key)) {
         throw new RuntimeException("DisruptorService.createAddHandlerStart1.key不为空!");
      } else {
         DisruptorCreateMethodEnum.checkKeyFormat(key);
         Tuple tuple = null;
         if (key.indexOf(DisruptorCreateMethodEnum.CREATE0.getName()) == 0) {
            tuple = this.create0(key);
         } else if (key.indexOf(DisruptorCreateMethodEnum.CREATE1.getName()) == 0) {
            tuple = this.create1(key);
         } else if (key.indexOf(DisruptorCreateMethodEnum.CREATE2.getName()) == 0) {
            tuple = this.create2(key);
         } else if (key.indexOf(DisruptorCreateMethodEnum.CREATE3.getName()) == 0) {
            tuple = this.create3(key);
         } else if (key.indexOf(DisruptorCreateMethodEnum.CREATE4.getName()) == 0) {
            tuple = this.create4(key);
         }

         if (Objects.nonNull(tuple)) {
            Disruptor disruptor = (Disruptor)tuple.get(0);
            AtomicBoolean started = (AtomicBoolean)tuple.get(2);
            if (Objects.nonNull(disruptor) && Objects.nonNull(started) && !started.get()) {
               disruptorHandler.buildHandler(key, disruptor);
            }

            this.start(key);
         }

         return tuple;
      }
   }

   public Boolean start(String key) {
      if (StringUtils.isBlank(key)) {
         throw new RuntimeException("DisruptorService.start.key不为空!");
      } else {
         DisruptorCreateMethodEnum.checkKeyFormat(key);
         Tuple tuple = (Tuple)this.disruptorMaps.get(key);
         if (Objects.isNull(tuple)) {
            throw new RuntimeException("DisruptorService.start.根据key未匹配到tuple!");
         } else {
            AtomicBoolean started = (AtomicBoolean)tuple.get(2);
            if (Objects.nonNull(started) && !started.compareAndSet(false, true)) {
               return Boolean.TRUE;
            } else {
               Disruptor disruptor = (Disruptor)tuple.get(0);
               if (Objects.nonNull(disruptor)) {
                  disruptor.start();
                  this.disruptorMaps.put(key, new Tuple(new Object[]{disruptor, disruptor.getRingBuffer(), started}));
                  LogUtils.info("DisruptorService.start.disruptor:{}==>成功!", new Object[]{disruptor});
                  return Boolean.TRUE;
               } else {
                  return Boolean.FALSE;
               }
            }
         }
      }
   }

   public void shutdown(String key) {
      if (StringUtils.isBlank(key)) {
         throw new RuntimeException("DisruptorService.start.key不为空!");
      } else {
         DisruptorCreateMethodEnum.checkKeyFormat(key);
         Tuple tuple = (Tuple)this.disruptorMaps.get(key);
         if (Objects.nonNull(tuple) && Objects.nonNull(tuple.get(0))) {
            Disruptor disruptor = (Disruptor)tuple.get(0);
            disruptor.shutdown();
            LogUtils.info("DisruptorService.shutdown.disruptor:{}==>关闭成功!", new Object[]{disruptor});
            this.disruptorMaps.entrySet().removeIf((e) -> key.equals(e.getKey()));
         }

      }
   }

   public void pushEvent0(String key, Object o1) {
      if (StringUtils.isBlank(key)) {
         throw new RuntimeException("DisruptorService.pushEvent0.key不为空!");
      } else {
         DisruptorCreateMethodEnum.checkKeyFormat(key);
         if (Objects.isNull(o1)) {
            throw new RuntimeException("pushEvent0.o1不为空!");
         } else {
            Tuple tuple = (Tuple)this.disruptorMaps.get(key);
            if (Objects.isNull(tuple)) {
               throw new RuntimeException("DisruptorService.pushEvent0.根据key未匹配到tuple!");
            } else {
               Disruptor disruptor = (Disruptor)tuple.get(0);
               if (Objects.isNull(disruptor)) {
                  throw new RuntimeException("pushEvent0.未找到对应key的disruptor,请检查key参数是否正确!");
               } else {
                  RingBuffer ringBuffer = disruptor.getRingBuffer();
                  if (Objects.isNull(ringBuffer)) {
                     throw new RuntimeException("pushEvent0.ringBuffer不为空!");
                  } else {
                     EventTranslatorOneArg<DisruptorEvent, Object> eventTranslatorOneArg = (event, sequence, arg0) -> event.setO1(o1);
                     ringBuffer.publishEvent(eventTranslatorOneArg, o1);
                  }
               }
            }
         }
      }
   }

   public void pushEvent1(String key, Object o1, Object o2) {
      if (StringUtils.isBlank(key)) {
         throw new RuntimeException("DisruptorService.pushEvent1.key不为空!");
      } else {
         DisruptorCreateMethodEnum.checkKeyFormat(key);
         if (Objects.isNull(o1)) {
            throw new RuntimeException("pushEvent1.o1不为空!");
         } else if (Objects.isNull(o2)) {
            throw new RuntimeException("pushEvent1.o2不为空!");
         } else {
            Tuple tuple = (Tuple)this.disruptorMaps.get(key);
            if (Objects.isNull(tuple)) {
               throw new RuntimeException("DisruptorService.pushEvent1.根据key未匹配到tuple!");
            } else {
               Disruptor disruptor = (Disruptor)tuple.get(0);
               if (Objects.isNull(disruptor)) {
                  throw new RuntimeException("pushEvent1.未找到对应key的disruptor,请检查key参数是否正确!");
               } else {
                  RingBuffer ringBuffer = disruptor.getRingBuffer();
                  if (Objects.isNull(ringBuffer)) {
                     throw new RuntimeException("pushEvent1.ringBuffer不为空!");
                  } else {
                     EventTranslatorTwoArg<DisruptorEvent, Object, Object> eventTranslatorTwoArg = (event, sequence, arg0, arg1) -> {
                        event.setO1(o1);
                        event.setO2(o2);
                     };
                     ringBuffer.publishEvent(eventTranslatorTwoArg, o1, o2);
                  }
               }
            }
         }
      }
   }

   public void pushEvent2(String key, Object o1, Object o2, Object o3) {
      if (StringUtils.isBlank(key)) {
         throw new RuntimeException("DisruptorService.pushEvent2.key不为空!");
      } else {
         DisruptorCreateMethodEnum.checkKeyFormat(key);
         if (Objects.isNull(o1)) {
            throw new RuntimeException("pushEvent2.o1不为空!");
         } else if (Objects.isNull(o2)) {
            throw new RuntimeException("pushEvent2.o2不为空!");
         } else if (Objects.isNull(o3)) {
            throw new RuntimeException("pushEvent2.o3不为空!");
         } else {
            Tuple tuple = (Tuple)this.disruptorMaps.get(key);
            if (Objects.isNull(tuple)) {
               throw new RuntimeException("DisruptorService.pushEvent2.根据key未匹配到tuple!");
            } else {
               Disruptor disruptor = (Disruptor)tuple.get(0);
               if (Objects.isNull(disruptor)) {
                  throw new RuntimeException("pushEvent2.未找到对应key的disruptor,请检查key参数是否正确!");
               } else {
                  RingBuffer ringBuffer = disruptor.getRingBuffer();
                  if (Objects.isNull(ringBuffer)) {
                     throw new RuntimeException("pushEvent2.ringBuffer不为空!");
                  } else {
                     EventTranslatorThreeArg<DisruptorEvent, Object, Object, Object> eventTranslatorThreeArg = (event, sequence, arg0, arg1, arg2) -> {
                        event.setO1(o1);
                        event.setO2(o2);
                        event.setO3(o3);
                     };
                     ringBuffer.publishEvent(eventTranslatorThreeArg, o1, o2, o3);
                  }
               }
            }
         }
      }
   }

   public void pushEvent3(String key, List<Object> objectList) {
      if (StringUtils.isBlank(key)) {
         throw new RuntimeException("DisruptorService.pushEvent3.key不为空!");
      } else {
         DisruptorCreateMethodEnum.checkKeyFormat(key);
         if (CollectionUtils.isNotEmpty(objectList)) {
            throw new RuntimeException("pushEvent2.objectList不为空!");
         } else {
            Tuple tuple = (Tuple)this.disruptorMaps.get(key);
            if (Objects.isNull(tuple)) {
               throw new RuntimeException("DisruptorService.pushEvent3.根据key未匹配到tuple!");
            } else {
               Disruptor disruptor = (Disruptor)tuple.get(0);
               if (Objects.isNull(disruptor)) {
                  throw new RuntimeException("pushEvent3.未找到对应key的disruptor,请检查key参数是否正确!");
               } else {
                  RingBuffer ringBuffer = disruptor.getRingBuffer();
                  if (Objects.isNull(ringBuffer)) {
                     throw new RuntimeException("pushEvent2.ringBuffer不为空!");
                  } else {
                     EventTranslatorOneArg<DisruptorEvent, Object> eventTranslatorOneArg = (event, sequence, arg0) -> event.setObjectList(objectList);
                     ringBuffer.publishEvent(eventTranslatorOneArg, objectList);
                  }
               }
            }
         }
      }
   }

   public void destroy() {
      LogUtils.info("DisruptorService.destroy.keyMaps.size:{}", new Object[]{this.keyMaps.size()});
      if (CollectionUtils.isNotEmpty(this.keyMaps)) {
         for(Map.Entry<String, Tuple> entry : this.keyMaps.entrySet()) {
            Tuple tuple = (Tuple)entry.getValue();
            if (!Objects.isNull(tuple)) {
               this.shutdown((String)tuple.get(1));
               LogUtils.info("key:{}的disruptor关闭完成!", new Object[]{entry.getKey()});
            }
         }
      }

   }
}
