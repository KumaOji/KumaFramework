package com.kuma.boot.eventbus.disruptor.tmp4;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.kuma.boot.eventbus.disruptor.tmp4.event.DistributeEvent;
import com.kuma.boot.eventbus.disruptor.tmp4.event.EventContext;
import com.kuma.boot.eventbus.disruptor.tmp4.thread.DelayEventQueue;
import com.kuma.boot.eventbus.disruptor.tmp4.thread.NamedThreadFactoryManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DistributorLine {
   private final long sleepNanos = 5L;
   private final ExecutorService executorService;
   private final DelayEventQueue<EventTranslator<DistributeEvent>> delayQueue;
   private final Disruptor<DistributeEvent> distributorLine;

   public DistributorLine(String name, int size, DelayEventQueue<EventTranslator<DistributeEvent>> delayQueue) {
      this.delayQueue = delayQueue;
      this.executorService = Executors.newSingleThreadExecutor(NamedThreadFactoryManager.INSTANCE.acquire(name + "Delay"));
      this.distributorLine = new Disruptor(DistributeEvent::new, size, (r) -> {
         Thread thread = new Thread(r);
         thread.setName(name);
         thread.setDaemon(true);
         return thread;
      }, ProducerType.MULTI, new YieldingWaitStrategy());
      this.distributorLine.handleEventsWith(new EventHandler[]{WorkerStationManager.INSTANCE});
   }

   public void publish(String targetStation, String targetTopic, EventContext context) {
      EventTranslator<DistributeEvent> translator = (ex, s) -> {
         while(!ex.isAvailable() || !ex.doing()) {
         }

         ex.setContext(context);
         ex.setTargetStation(targetStation);
         ex.setTargetTopic(targetTopic);
      };
      if (!this.distributorLine.getRingBuffer().tryPublishEvent(translator)) {
         try {
            this.delayQueue.put(translator);
         } catch (InterruptedException e) {
            throw new RuntimeException(e);
         }
      }

   }

   public void stop() {
      this.distributorLine.halt();
      this.executorService.shutdown();
   }

   public long hasPublishNum() {
      return this.distributorLine.getCursor() + 1L;
   }

   public void start() {
      this.executorService.submit(() -> {
         while(true) {
            if (!this.delayQueue.isEmpty()) {
               try {
                  this.distributorLine.getRingBuffer().publishEvent(this.delayQueue.take());
               } catch (InterruptedException e) {
                  throw e;
               }
            } else {
               try {
                  TimeUnit.NANOSECONDS.sleep(5L);
               } catch (InterruptedException e) {
                  throw new RuntimeException(e);
               }
            }
         }
      });
      this.distributorLine.start();
   }
}
